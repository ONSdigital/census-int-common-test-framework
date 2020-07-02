package uk.gov.ons.ctp.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/** Loads JSON representation of test DTOS for unit tests */
@Slf4j
public class FixtureHelper {

  /**
   * Find, deserialize and return List of dummy test objects from a json file. This method derives
   * the path and file name of the json file by looking at the class and only uses the package name
   * to derive the path to the fixture which has a file name "PackageFixture.", as well as the name
   * of the type you asked it to return.
   *
   * @param <T> the type of object we expect to load and return a List of
   * @param clazz the type
   * @return the list
   */
  public static <T> List<T> loadPackageFixtures(final Class<T[]> clazz) {
    String callerClassName = new Exception().getStackTrace()[1].getClassName();
    return actuallyLoadFixtures(clazz, callerClassName, null, null, true);
  }

  /**
   * Find, deserialize and return List of dummy test objects from a json file. This method derives
   * the path and file name of the json file by looking at the class and only uses the package name
   * to derive the path to the fixture which has a file name "PackageFixture.", as well as the name
   * of the type you asked it to return. The qualifier allows for multiple PackageFixture files
   *
   * @param <T> the type of object we expect to load and return a List of
   * @param clazz the type
   * @param qualifier added to file name to allow a class to have multiple forms of same type
   * @return the list
   */
  public static <T> List<T> loadPackageFixtures(final Class<T[]> clazz, final String qualifier) {
    String callerClassName = new Exception().getStackTrace()[1].getClassName();
    return actuallyLoadFixtures(clazz, callerClassName, null, null, true);
  }

  /**
   * Overloaded version
   *
   * @param <T> the type of object we expect to load and return a List of
   * @param clazz the type
   * @return the list
   */
  public static <T> List<T> loadMethodFixtures(final Class<T[]> clazz) {
    String callerClassName = new Exception().getStackTrace()[1].getClassName();
    return actuallyLoadFixtures(clazz, callerClassName, null, null, false);
  }

  /**
   * Find, deserialize and return List of dummy test objects from a json file This method derives
   * the path and file name of the json file by looking at the class and method that called it, as
   * well as the name of the type you asked it to return.
   *
   * @param <T> the type of object we expect to load and return a List of
   * @param clazz the type
   * @param qualifier added to file name to allow a class to have multiple forms of same type
   * @return the list
   */
  public static <T> List<T> loadMethodFixtures(final Class<T[]> clazz, final String qualifier) {
    String callerClassName = new Exception().getStackTrace()[1].getClassName();
    String callerMethodName = new Exception().getStackTrace()[1].getMethodName();
    return actuallyLoadFixtures(clazz, callerClassName, callerMethodName, qualifier, false);
  }

  /**
   * Overloaded version
   *
   * @param <T> the type of object we expect to load and return a List of
   * @param clazz the type
   * @return the list
   */
  public static <T> List<T> loadClassFixtures(final Class<T[]> clazz) {
    String callerClassName = new Exception().getStackTrace()[1].getClassName();
    return actuallyLoadFixtures(clazz, callerClassName, null, null, false);
  }

  /**
   * Find, deserialize and return List of dummy test objects from a json file This method derives
   * the path and file name of the json file by looking at the class and method that called it, as
   * well as the name of the type you asked it to return.
   *
   * @param <T> the type of object we expect to load and return a List of
   * @param clazz the type
   * @param qualifier added to file name to allow a class to have multiple forms of same type
   * @return the list
   */
  public static <T> List<T> loadClassFixtures(final Class<T[]> clazz, final String qualifier) {
    String callerClassName = new Exception().getStackTrace()[1].getClassName();
    return actuallyLoadFixtures(clazz, callerClassName, null, qualifier, false);
  }

  public static ObjectNode loadClassObjectNode() {
    String callerClassName = new Exception().getStackTrace()[1].getClassName();
    return actuallyLoadObjectNode(callerClassName, null, null, false);
  }

  public static ObjectNode loadClassObjectNode(final String qualifier) {
    String callerClassName = new Exception().getStackTrace()[1].getClassName();
    return actuallyLoadObjectNode(callerClassName, null, qualifier, false);
  }

  public static ObjectNode loadPackageObjectNode() {
    String callerClassName = new Exception().getStackTrace()[1].getClassName();
    return actuallyLoadObjectNode(callerClassName, null, null, true);
  }

  public static ObjectNode loadPackageObjectNode(final String qualifier) {
    String callerClassName = new Exception().getStackTrace()[1].getClassName();
    return actuallyLoadObjectNode(callerClassName, null, qualifier, true);
  }

  /**
   * Actually does the dummy loading!
   *
   * @param clazz the type
   * @param <T> the type of object we expect to load and return a List of
   * @param callerClassName name of the class that made the initial call
   * @param callerMethodName name of the method that made the initial call
   * @param qualifier added to file name to allow a class to have multiple forms of same type
   * @param packageOnly true if the class and method name are not be used but instead the test class
   *     package name only
   * @return the loaded dummies of the the type T in a List
   */
  private static <T> List<T> actuallyLoadFixtures(
      final Class<T[]> clazz,
      final String callerClassName,
      final String callerMethodName,
      final String qualifier,
      final boolean packageOnly) {
    List<T> dummies = null;
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    String clazzName = clazz.getSimpleName().replaceAll("[\\[\\]]", "");
    String path =
        generatePath(callerClassName, clazzName, callerMethodName, qualifier, packageOnly);
    try {
      File file = new File(ClassLoader.getSystemResource(path).getFile());
      dummies = Arrays.asList(mapper.readValue(file, clazz));
    } catch (Throwable t) {
      log.debug("Problem loading fixture {} reason {}", path, t.getMessage());
      throw new RuntimeException("Failed to load fixture: " + path);
    }
    return dummies;
  }

  /**
   * Actually does the dummy loading!
   *
   * @param <T> the type of object we expect to load and return a List of
   * @param callerClassName name of the class that made the initial call
   * @param callerMethodName name of the method that made the initial call
   * @param qualifier added to file name to allow a class to have multiple forms of same type
   * @param packageOnly true if the class and method name are not be used but instead the test class
   *     package name only
   * @return the loaded dummies of the the type T in a List
   */
  private static ObjectNode actuallyLoadObjectNode(
      final String callerClassName,
      final String callerMethodName,
      final String qualifier,
      final boolean packageOnly) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    ObjectNode jsonNode = null;
    String path = generatePath(callerClassName, null, callerMethodName, qualifier, packageOnly);
    try {
      File file = new File(ClassLoader.getSystemResource(path).getFile());
      jsonNode = (ObjectNode) mapper.readTree(file);
    } catch (Throwable t) {
      log.debug("Problem loading fixture {} reason {}", path, t.getMessage());
      throw new RuntimeException("Failed to load fixture: " + path);
    }
    return jsonNode;
  }

  /**
   * Format the path name to the json file, using optional params ie
   * "uk/gov/ons/ctp/response/action/thing/ThingTest.testThingOK.blueThings.json"
   *
   * @param callerClassName the name of the class that made the initial call
   * @param clazzName the type of object to deserialize and return in a List
   * @param methodName the name of the method in the callerClass that made the initial call
   * @param qualifier further quaification is a single method may need to have two collections of
   *     the same type, qualified
   * @param packageOnly true if the class and method name are not be used but instead the test class
   *     package name only
   * @return the constructed path string
   */
  private static String generatePath(
      final String callerClassName,
      final String clazzName,
      final String methodName,
      final String qualifier,
      final boolean packageOnly) {

    String path = callerClassName;
    if (packageOnly) {
      path = path.replaceAll("\\.\\w*$", ".PackageFixture");
    }
    path =
        path.replaceAll("\\.", "/")
            + "."
            + ((methodName != null && !packageOnly) ? (methodName + ".") : "")
            + ((clazzName != null) ? (clazzName + ".") : "")
            + ((qualifier != null) ? (qualifier + ".") : "")
            + "json";

    return path;
  }
}
