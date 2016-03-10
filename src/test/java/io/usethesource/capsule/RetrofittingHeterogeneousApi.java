package io.usethesource.capsule;

import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.Test;

public class RetrofittingHeterogeneousApi {

  @Test
  public void testIntAndBigIntSetImpl() {
    IntAndBigIntSetImpl impl = new IntAndBigIntSetImpl();
    impl.add(new EitherIntOrGeneric<BigInteger>());
    impl.add(BigInteger.ZERO);
    impl.add(1);
    System.out.println();
  }
  
  @Test
  public void testSetImpl() {
    SetImpl<Number> impl = new SetImpl<>();
    impl.add(new Integer(5));
    impl.add(BigInteger.ZERO);
    impl.add(1);
    System.out.println();
  }
  
  @Test
  public void testSetImpl2() {
    SetImpl<BigInteger> impl = new SetImpl<>();
    impl.add(new Integer(5));
    impl.add(BigInteger.ZERO);
    impl.add(1);    
    System.out.println();
  }  

  
  @Test
  public void heterogeneousInterfaceTest() {
    put(String.class, "abc", int.class, 5);
    put(String.class, "abc", Integer.class, 5);
    
    put(String.class, "abc", long.class, 5L);
    put(String.class, "abc", Long.class, 5L);    
  }  
    
  static <T, U> void put(Class<T> keyType, T keyInstance, Class<U> valueType, U valueInstance) {  
    System.out.println();
    
    System.out.println(keyType.getName());
    System.out.println(valueType.getName());  
       
    switch(keyType.getName()) {
      case "java.lang.String":
        switch(valueType.getName()) {
          case "int":
            put((String) keyType.cast(keyInstance), (int) (Integer) valueInstance);
            return;
          case "java.lang.Integer":
            put((String) keyType.cast(keyInstance), (Integer) valueInstance);
            return;
        }
    }
          
    System.out.println("Unsupported Type");    
  }
  
  static void put(String keyInstance, Integer valueInstance) {  
    System.out.println("put(String keyInstance, Integer valueInstance)");
//    System.out.println(keyInstance);
//    System.out.println(valueInstance);
  }
  
  static void put(String keyInstance, int valueInstance) {  
    System.out.println("put(String keyInstance, int valueInstance)");
//    System.out.println(keyInstance);
//    System.out.println(valueInstance);
  }
  
}

class Either2<K, V> {
  
}

class EitherIntOrGeneric<V> {
  
}

class IntAndBigIntSetImpl implements IntAndBigIntSet {

  @Override
  public boolean add(EitherIntOrGeneric<BigInteger> e) {
    System.out.println("IntAndBigIntSetImpl.add(EitherIntOrGeneric)");
    return true;    
  }
  
  @Override
  public boolean add(BigInteger value) {
    System.out.println("IntAndBigIntSetImpl.add(BigInteger)");
    return true;    
  }
  
  @Override
  public boolean add(int value) {
    System.out.println("IntAndBigIntSetImpl.add(int)");
    return true;
  }
  
}

interface IntAndBigIntSet extends Set<EitherIntOrGeneric<BigInteger>> {
  
  @Override
  boolean add(EitherIntOrGeneric<BigInteger> e);
  
  boolean add(BigInteger value);
  
//  @Override
//  boolean add(int value);
  
}

class SetImpl<E> implements Set<E> {
 
  @Override
  public boolean add(E value) {
    System.out.println("SetImpl.add(E)");
    return true;    
  }
  
  @Override
  public boolean add(int value) {
    System.out.println("SetImpl.add(int)");
    return true;
  }
  
}

interface Set<E> extends Collection<E> {  
  
  boolean add(E e);
  
  default boolean add(int value) {
    try {
      @SuppressWarnings("unchecked")
      E typedValue = ((E) Integer.valueOf(value));     
      return add(typedValue);
    } catch (ClassCastException e) {
      return false;
    }
  }
  
}

interface Collection<E> { // extends Iterable<E> {
  
  boolean add(E e);
  
}

interface HeterogeneousSet {
  <E> boolean add      (Class<E> elementType, E element);
  <E> boolean remove   (Class<E> elementType, E element);
  <E> boolean contains (Class<E> elementType, E element);
}

interface HeterogeneousMap { 
  // pull-based dispatch on type
  <K, V> TypedObject<?> put    (Class<K> keyType, K key, Class<V> valueType, V value);
  <K, V> TypedObject<?> remove (Class<K> keyType, K key);
  <K, V> TypedObject<?> get    (Class<K> keyType, K key);   
  
  // push-based dispatch on type
  <K, V> void put    (Class<K> keyType, K key, Class<V> valueType, V value, CallbackMap callbacks);
  <K, V> void remove (Class<K> keyType, K key, Class<V> valueType, V value, CallbackMap callbacks);
  <K, V> void get    (Class<K> keyType, K key, Class<V> valueType, V value, CallbackMap callbacks);
}

interface TypedObject<T> {
  Class<T> getType();
  T get();
}

interface CallbackMap {
  <E> Consumer<E> put (Class<E> elementType, Consumer<E> consumer);
  <E> Consumer<E> get (Class<E> elementType);
}