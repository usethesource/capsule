/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;

import javax.tools.*;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

import io.usethesource.capsule.core.deprecated.TrieSet_5Bits;
import io.usethesource.capsule.experimental.lazy.TrieSet_5Bits_LazyHashCode;
import io.usethesource.capsule.experimental.memoized.TrieSet_5Bits_Memoized_LazyHashCode;
import io.usethesource.capsule.experimental.specialized.TrieSet_5Bits_Spec0To8;

@RunWith(Suite.class)
public class RuntimeCodeGenerationTestSuite extends Suite {

  public RuntimeCodeGenerationTestSuite(Class<?> setupClass) throws InitializationError {
    super(setupClass, getClasses(setupClass));
  }

  private static Class<?>[] getClasses(Class<?> setupClass) {
     final List<Class<?>> suiteClasses = new ArrayList<Class<?>>();

    // @formatter:off
    final List<Class> componentTypes = Arrays.asList(Integer.class);
    final List<Class> setTypes = Arrays.asList(
        TrieSet_5Bits.class
        , TrieSet_5Bits_Spec0To8.class
        , TrieSet_5Bits_LazyHashCode.class
        , TrieSet_5Bits_Memoized_LazyHashCode.class
    );

    final String javaClassNameTemplate = "io.usethesource.capsule.$CLASS_NAME$$COMPONENT_TYPE$SetPropertiesTest";
    final String javaSourceCodeTemplate =
        "package io.usethesource.capsule;\n" +
        "import $QUALIFIED_CLASS_NAME$;\n" +
        "@org.junit.runner.RunWith(com.pholser.junit.quickcheck.runner.JUnitQuickcheck.class)\n" +
        "public class $CLASS_NAME$$COMPONENT_TYPE$SetPropertiesTest extends AbstractSetProperties<$COMPONENT_TYPE$, $CLASS_NAME$<$COMPONENT_TYPE$>> {\n" +
        "  public $CLASS_NAME$$COMPONENT_TYPE$SetPropertiesTest() {\n" +
        "    super($CLASS_NAME$.class);\n" +
        "  }\n" +
        "}\n";
    // @formatter:on


    setTypes.stream().flatMap(setKlass -> componentTypes.stream().map(componentKlass -> {
      try {
        // @formatter:off
        final String className = javaClassNameTemplate
            .replace("$COMPONENT_TYPE$", componentKlass.getSimpleName())
            .replace("$QUALIFIED_CLASS_NAME$", setKlass.getName())
            .replace("$CLASS_NAME$", setKlass.getSimpleName());

        final String sourceCode = javaSourceCodeTemplate
            .replace("$COMPONENT_TYPE$", componentKlass.getSimpleName())
            .replace("$QUALIFIED_CLASS_NAME$", setKlass.getName())
            .replace("$CLASS_NAME$", setKlass.getSimpleName());
        // @formatter:on

        Class aClass = RuntimeJavaCompiler.compile(className, sourceCode);
        return aClass;
      } catch (Throwable throwable) {
        throw new RuntimeException(throwable);
      }
    })).forEach(suiteClasses::add);

    return suiteClasses.toArray(new Class<?>[0]);
  }

  private String subsituteComponentType(String template, Class<?> klass) {
    return template.replace("$COMPONENT_TYPE$", klass.getSimpleName());
  }

  private String subsituteSetType(String template, Class<?> klass) {
    // @formatter:off
    return template
        .replace("$QUALIFIED_CLASS_NAME$", klass.getName())
        .replace("$CLASS_NAME$", klass.getSimpleName());
    // @formatter:on
  }

  private static class RuntimeJavaCompiler {
    static final JavaCompiler JAVA_COMPILER = ToolProvider.getSystemJavaCompiler();

    public static Class<?> compile(String className, String sourceCode) throws Exception {
      // re-use current class path for compilation
      final String classPath = System.getProperty("java.class.path");
      final Iterable<String> options = Arrays.asList("-classpath", classPath);

      JavaFileObject src = new JavaSourceFromString(className, sourceCode);

      CustomClassLoader classLoader = new CustomClassLoader(ClassLoader.getSystemClassLoader());
      CustomJavaFileManager fileManager = new CustomJavaFileManager(
          JAVA_COMPILER.getStandardFileManager(null, null, null), classLoader);

      JavaCompiler.CompilationTask task =
          JAVA_COMPILER.getTask(null, fileManager, null, options, null, Collections.singleton(src));
      boolean result = task.call();

      return classLoader.loadClass(className);
    }
  }

  private  static class JavaSourceFromString extends SimpleJavaFileObject {
    private final String sourceCode;

    private static final URI toURI(String className) {
      return URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension);
    }

    public JavaSourceFromString(String className, String sourceCode) throws Exception {
      super(toURI(className), Kind.SOURCE);
      this.sourceCode = sourceCode;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
      return sourceCode;
    }
  }

  private  static class ByteCodeFromString extends SimpleJavaFileObject {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

     private static final URI toURI(String className) throws Exception {
       return new URI(className);
     }

    public ByteCodeFromString(String className) throws Exception {
      super(toURI(className), Kind.CLASS);
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
      return outputStream; // already opened at construction
    }

    public byte[] getByteCode() {
      return outputStream.toByteArray();
    }
  }

  private  static class CustomClassLoader extends ClassLoader {
    private final Map<String, ByteCodeFromString> classes = new HashMap<>();

    public CustomClassLoader(ClassLoader parentClassLoader) {
      super(parentClassLoader);
    }

    public void setByteCode(ByteCodeFromString javaFile) {
      classes.put(javaFile.getName(), javaFile);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
      ByteCodeFromString javaFile = classes.get(name);
      if (javaFile != null) {
        byte[] byteCode = javaFile.getByteCode();
        return defineClass(name, byteCode, 0, byteCode.length);
      }
      return super.findClass(name);
    }
  }

  private static class CustomJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
    private final CustomClassLoader classLoader;

    protected CustomJavaFileManager(JavaFileManager fileManager, CustomClassLoader classLoader) {
      super(fileManager);
      this.classLoader = classLoader;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className,
        JavaFileObject.Kind kind, FileObject sibling) {
      try {
        ByteCodeFromString javaFile = new ByteCodeFromString(className);
        classLoader.setByteCode(javaFile);
        return javaFile;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public ClassLoader getClassLoader(JavaFileManager.Location location) {
      return classLoader;
    }
  }

}
