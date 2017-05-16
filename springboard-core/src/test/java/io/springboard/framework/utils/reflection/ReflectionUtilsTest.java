package io.springboard.framework.utils.reflection;

import static org.junit.Assert.assertEquals;
import io.springboard.framework.utils.reflection.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import org.junit.Test;

@SuppressWarnings("rawtypes")
public class ReflectionUtilsTest {
	@Test
	public void getAndSetFieldValue() {
		TestBean bean = new TestBean();
		//无需getter函数, 直接读取privateField
		assertEquals(1, ReflectionUtils.getFieldValue(bean, "privateField"));
		//绕过将publicField+1的getter函数,直接读取publicField的原始值
		assertEquals(1, ReflectionUtils.getFieldValue(bean, "publicField"));

		bean = new TestBean();
		//无需setter函数, 直接设置privateField
		ReflectionUtils.setFieldValue(bean, "privateField", 2);
		assertEquals(2, bean.inspectPrivateField());

		//绕过将publicField+1的setter函数,直接设置publicField的原始值
		ReflectionUtils.setFieldValue(bean, "publicField", 2);
		assertEquals(2, bean.inspectPublicField());
	}

	@Test
	public void invokeGetterAndSetter() {
		TestBean bean = new TestBean();
		assertEquals(bean.inspectPublicField() + 1, ReflectionUtils.invokeGetterMethod(bean, "publicField"));

		bean = new TestBean();
		ReflectionUtils.invokeSetterMethod(bean, "publicField", 10, int.class);
		assertEquals(10 + 1, bean.inspectPublicField());
	}

	@Test
	public void invokeMethod() {
		TestBean bean = new TestBean();
		assertEquals("hello calvin", ReflectionUtils.invokeMethod(bean, "privateMethod", new Class[] { String.class },
				new Object[] { "calvin" }));
	}

	@Test
	public void getSuperClassGenricType() {
		//获取第1，2个泛型类型
		assertEquals(String.class, ReflectionUtils.getSuperClassGenricType(TestBean.class));
		assertEquals(Long.class, ReflectionUtils.getSuperClassGenricType(TestBean.class, 1));

		//定义父类时无泛型定义
		assertEquals(Object.class, ReflectionUtils.getSuperClassGenricType(TestBean2.class));

		//无父类定义
		assertEquals(Object.class, ReflectionUtils.getSuperClassGenricType(TestBean3.class));
	}
	
	@Test
	public void testGenricType(){
		System.out.println("获取object的泛型类型：");
		TestBean10<String> test = new TestBean10<String>(){}; // 匿名内部类的声明在编译时进行，实例化在运行时进行  
		Class clazz = ReflectionUtils.getSuperClassGenricType(test.getClass());
		System.out.println(clazz.getName());
          
        //====================================================================
        System.out.println("\n获取方法参数的泛型类型");
		TestBean10<String> test2 = new TestBean10<String>(){};
		Method method = ReflectionUtils.getAccessibleMethod(test2, "applyRef1", Map.class, Vector.class);
		Type[] pType = method.getParameterTypes();
        Type[] gpType = method.getGenericParameterTypes();
        System.out.println(pType[0]) ;
        System.out.println(pType[1]) ;
        System.out.println(gpType[0]) ;
        System.out.println(gpType[1]) ;
	}

	@Test
	public void convertReflectionExceptionToUnchecked() {
		IllegalArgumentException iae = new IllegalArgumentException();
		//ReflectionException,normal
		RuntimeException e = ReflectionUtils.convertReflectionExceptionToUnchecked(iae);
		assertEquals(iae, e.getCause());
		assertEquals("Reflection Exception.", e.getMessage());

		//InvocationTargetException,extract it's target exception.
		Exception ex = new Exception();
		e = ReflectionUtils.convertReflectionExceptionToUnchecked(new InvocationTargetException(ex));
		assertEquals(ex, e.getCause());
		assertEquals("Reflection Exception.", e.getMessage());

		//UncheckedException, ignore it.
		RuntimeException re = new RuntimeException("abc");
		e = ReflectionUtils.convertReflectionExceptionToUnchecked(re);
		assertEquals("abc", e.getMessage());

		//Unexcepted Checked exception.
		e = ReflectionUtils.convertReflectionExceptionToUnchecked(ex);
		assertEquals("Unexpected Checked Exception.", e.getMessage());

	}

	public static class ParentBean<T, PK> {
	}

	public static class TestBean extends ParentBean<String, Long> {
		/** 没有getter/setter的field */
		private int privateField = 1;
		/** 有getter/setter的field */
		private int publicField = 1;

		public int getPublicField() {
			return publicField + 1;
		}

		public void setPublicField(int publicField) {
			this.publicField = publicField + 1;
		}

		public int inspectPrivateField() {
			return privateField;
		}

		public int inspectPublicField() {
			return publicField;
		}

		@SuppressWarnings("unused")
		private String privateMethod(String text) {
			return "hello " + text;
		}
	}

	public static class TestBean2 extends ParentBean {
	}

	public static class TestBean3 {

		private int id;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	}
	
	public class TestBean10<T> {
		public T applyRef1(Map<String,Integer> map, Vector<Date> v){
			return null;
		}
	}
}
