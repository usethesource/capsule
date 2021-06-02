/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import static io.usethesource.capsule.StaticConfiguration.*;
import static org.junit.Assert.*;

@RunWith(JUnitQuickcheck.class)
public class MapEntryPropertiesTest {

  @Property(trials = DEFAULT_TRIALS)
  public void serializationRoundtrip(Map.Entry<String, String> input) throws Exception {
    assertEquals(input, deserialize(serialize((Serializable) input), input.getClass()));
  }

  private static <T extends Serializable> byte[] serialize(T item) throws IOException {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      oos.writeObject(item);
      return baos.toByteArray();
    } catch (IOException e) {
      throw e;
    }
  }

  private static <T> T deserialize(byte[] bytes, Class<T> itemClass)
      throws IOException, ClassNotFoundException {
    try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais)) {
      Object item = ois.readObject();
      return itemClass.cast(item);
    } catch (IOException | ClassNotFoundException e) {
      throw e;
    }
  }

}