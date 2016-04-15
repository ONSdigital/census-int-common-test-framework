package uk.gov.ons.ctp.common;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Some individual methods for unit tests to reuse
 *
 */
public class TestHelper {

  /**
   * Overloaded version
   * 
   * @param clazz the type
   * @return the list
   * @throws Exception failed to load - does the json file exist alongside the
   *           calling class in the classpath?
   */
  public static <T> List<T> loadMethodDummies(Class<T[]> clazz) throws Exception {
    String callerClassName = new Exception().getStackTrace()[1].getClassName();
    return actuallyLoadDummies(clazz, callerClassName, null, null);
  }

  /**
   * Find, deserialize and return List of dummy test objects from a json file
   * This method derives the path and file name of the json file by looking at
   * the class and method that called it, as well as the name of the type you
   * asked it to return.
   * 
   * @param clazz the type
   * @param qualifier added to file name to allow a class to have multiple forms
   *          of same type
   * @return the list
   * @throws Exception failed to load - does the json file exist alongside the
   *           calling class in the classpath?
   */
  public static <T> List<T> loadMethodDummies(Class<T[]> clazz, String qualifier) throws Exception {
    String callerClassName = new Exception().getStackTrace()[1].getClassName();
    String callerMethodName = new Exception().getStackTrace()[1].getMethodName();
    return actuallyLoadDummies(clazz, callerClassName, callerMethodName, qualifier);
  }
  

  /**
   * Overloaded version
   * 
   * @param clazz the type
   * @return the list
   * @throws Exception failed to load - does the json file exist alongside the
   *           calling class in the classpath?
   */
  public static <T> List<T> loadClassDummies(Class<T[]> clazz) throws Exception {
    String callerClassName = new Exception().getStackTrace()[1].getClassName();
    return actuallyLoadDummies(clazz, callerClassName, null, null);
  }

  /**
   * Find, deserialize and return List of dummy test objects from a json file
   * This method derives the path and file name of the json file by looking at
   * the class and method that called it, as well as the name of the type you
   * asked it to return.
   * 
   * @param clazz the type
   * @param qualifier added to file name to allow a class to have multiple forms
   *          of same type
   * @return the list
   * @throws Exception failed to load - does the json file exist alongside the
   *           calling class in the classpath?
   */
  public static <T> List<T> loadClassDummies(Class<T[]> clazz, String qualifier) throws Exception {
    String callerClassName = new Exception().getStackTrace()[1].getClassName();
    return actuallyLoadDummies(clazz, callerClassName, null, qualifier);
 
  }

  /**
   * Actually does the dummy loading!
   * @param clazz the type
   * @param callerClassName name of the class that made the initial call
   * @param callerMethodName name of the method that made the initial call
   * @param qualifier added to file name to allow a class to have multiple forms
   *          of same type
   * @return the loaded dummies of the the type T in a List
   * @throws Exception
   */
  private static <T> List<T> actuallyLoadDummies(Class<T[]> clazz, String callerClassName, String callerMethodName, String qualifier) throws Exception {
    List<T> dummies = null;
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    String clazzName = clazz.getSimpleName().replaceAll("[\\[\\]]", "");
    String path = generatePath(callerClassName, clazzName, callerMethodName, qualifier);
    File file = new File(ClassLoader.getSystemResource(path).getFile());
    dummies = Arrays.asList(mapper.readValue(file, clazz));
    return dummies;
  }
  
  /**
   * Format the path name to the json file, using optional params
   *  ie "uk/gov/ons/ctp/response/action/thing/ThingTest.testThingOK.blueThings.json"
   *  
   * @param callerClassName the name of the class that made the initial call
   * @param clazzName the type of object to deserialize and return in a List
   * @param methodName the name of the method in the callerClass that made the initial call
   * @param qualifier further quaification is a single method may need to have two collections of the same type, qualified
   * @return
   */
  private static String generatePath(String callerClassName, String clazzName, String methodName, String qualifier) {
    return callerClassName.replaceAll("\\.", "/") + "." + ((methodName != null) ? (methodName + ".") : "") + clazzName + "."
        + ((qualifier != null) ? (qualifier + ".") : "")  + "json";
  }

  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  /**
   * Creates an instance of the target class, using its default constructor, and
   * invokes the private method, passing the provided params.
   * 
   * @param target the Class owning the provate method
   * @param methodName the name of the private method we wish to invoke
   * @param params the params we wish to send to the private method
   * @return the object that came back from the method!
   * @throws Exception Something went wrong with reflection, Get over it.
   */
  public static Object callPrivateMethodOfDefaultConstructableClass(final Class<?> target,
      final String methodName,
      final Object... params)
      throws Exception {
    Constructor<?> constructor = target.getConstructor();
    Object instance = constructor.newInstance();
    Class<?>[] parameterTypes = new Class[params.length];
    for (int i = 0; i < params.length; i++) {
      parameterTypes[i] = params[i].getClass();
    }
    Method methodUnderTest = instance.getClass().getDeclaredMethod(methodName, parameterTypes);
    methodUnderTest.setAccessible(true);
    return methodUnderTest.invoke(instance, params);
  }
}
