/**
 * KYJ
 * 2015. 10. 11.
 */
package kyj.Fx.dao.wizard.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import javafx.beans.property.Property;
import kyj.Fx.dao.wizard.core.model.vo.FxDao;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.util.ReflectionUtil;

/**
 * @author KYJ
 *
 */
public class FxDaoCommons {

	public boolean isPrimitiveType(Class<?> clazz) {
		return ReflectionUtil.isPrimitiveType(clazz);
	}

	/**
	 * 기본형이 아닌 타입인경우 import항목을 추가한다.
	 *
	 * @Date 2015. 10. 11.
	 * @param vo
	 * @param clazz
	 * @return
	 * @User KYJ
	 */
	public boolean addImport(FxDao vo, Class<?> clazz) {
		boolean flag = false;
		if (clazz == null)
			return flag;
		StringBuffer importPart = vo.getImportPart();
		if (!isPrimitiveType(clazz)) {
			// import문이 중복으로 들어가는것을 방지함.
			if (importPart.indexOf(clazz.getName()) < 0) {
				importPart.append("import ").append(clazz.getName()).append(";\n");
			}

			flag = true;
		}
		return flag;
	}

	/**
	 * 기본형이 아닌 타입인경우 import항목을 추가한다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 28.
	 * @param vo
	 * @param clazz
	 * @return
	 */
	public boolean addImport(FxDao vo, String clazz) {
		boolean flag = false;
		if (clazz == null)
			return flag;

		boolean isPrimitive = false;
		try {
			Class<?> forName = Class.forName(clazz);
			isPrimitive = isPrimitiveType(forName);
		} catch (Exception e) {
		}

		if (!isPrimitive) {
			StringBuffer importPart = vo.getImportPart();

			// import문이 중복으로 들어가는것을 방지함.
			if (importPart.indexOf(clazz) < 0) {
				importPart.append("import ").append(clazz).append(";\n");
			}

			flag = true;
		}
		return flag;
	}

	/**
	 * 문자열로 구성된 (패키지명 + 클래스명) 텍스트중에서 타입클래스이름을 반환
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 29.
	 * @param packageFullClassName
	 * @return
	 */
	public String getType(String packageFullClassName) {
		int indexOf = packageFullClassName.lastIndexOf('.');
		if (indexOf >= 0) {
			indexOf++;
			return packageFullClassName.substring(indexOf);
		}
		return "";
	}

	/**
	 * 변수명반환
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 29.
	 * @param packageFullClassName
	 * @return
	 */
	public String getVarName(String packageFullClassName) {
		String type = getType(packageFullClassName);
		return getIndexLowercase(type, 0);
	}

	public boolean isJavafxProperty(Class<?> fieldType) {
		return Property.class.isAssignableFrom(fieldType);
	}

	public String convertFxPropertyToPrimitive(Class<?> fieldType) throws Exception {
		String realType = "";

		if (fieldType != null) {
			Type[] genericInterfaces = fieldType.getGenericInterfaces();
			for (Type type : genericInterfaces) {

				if (type.getTypeName().startsWith("javafx.beans.property.Property")) {
					if (type instanceof ParameterizedType) {
						ParameterizedType imp = (ParameterizedType) type;
						Type[] actualTypeArguments = imp.getActualTypeArguments();
						// Type realtype = actualTypeArguments[0];
						Class<?> realtype = (Class<?>) actualTypeArguments[0];
						// String typeName = realtype.getTypeName();
						realType = realtype.getSimpleName();
						break;
					}
				}
			}
		}

		return realType;
	}

	/**
	 * 파라미터 클래스정보에 동일한 클래스타입이 존재하는지 확인한다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 28.
	 * @param params
	 * @return
	 */
	public boolean isDuplicated(List<ClassMeta> params) {

		if (params != null) {

			Set<String> set = new HashSet<>();
			for (ClassMeta meta : params) {
				String key = meta.getPackageName() + meta.getName();
				if (set.contains(key))
					return true;

				set.add(key);
			}
		}
		return false;
	}

	/**
	 * 텍스트 앞부분에 count수만큼 \t을 추가함.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 29.
	 * @param str
	 * @param count
	 * @return
	 */
	public String applyedTabKeys(String str, final int count) {
		return applyedTabKeys(str, count, null);
	}

	/**
	 * 텍스트 앞부분에 count수만큼 \t을 추가하고 추가적으로 텍스트앞에 붙을내용이 있으면 Supplier를 작성하여 붙인다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 29.
	 * @param str
	 * @param count
	 * @param preAppendText
	 * @return
	 */
	public String applyedTabKeys(String str, final int count, Supplier<String> preAppendText) {
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try {
			try {
				br = new BufferedReader(new StringReader(str));

				String temp = null;
				while ((temp = br.readLine()) != null) {

					for (int i = 0; i < count; i++) {
						sb.append("\t");
					}

					if (preAppendText != null) {
						sb.append(preAppendText.get());
					}

					sb.append(temp).append("\n");
				}
			} catch (Exception e) {
				sb.setLength(0);
			}

		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public enum IndexCaseTypes {
		UPPERCASE, LOWERCASE
	}

	/**
	 * str 문자열에서 index에 해당하는 부분만 대문자, 혹은 소문자로 고친다. (IndexCaseTypes에 따른 설정)
	 * 
	 * @param str
	 *            target
	 * @param index
	 *            character index
	 * @param type
	 *            lower or upper
	 * @return
	 */
	public static String getIndexcase(String str, int index, IndexCaseTypes type) {
		StringBuffer sb = new StringBuffer();
		if(str == null || str.isEmpty())
			return str;
		
		// DEFAULT UPPERCASE
		char indexChar = Character.toUpperCase(str.charAt(index));
		if (type == IndexCaseTypes.LOWERCASE) {
			indexChar = Character.toLowerCase(str.charAt(index));
		}

		switch (index) {
			case 0 :
				sb.append(indexChar).append(str.substring(index + 1));
				break;
			default :
				sb.append(str.substring(0, index)).append(indexChar).append(str.substring(index + 1));
				break;
		}
		return sb.toString();
	}

	/**
	 * str 문자열에서 index에 해당하는 부분만 대문자로 고친다.
	 * 
	 * @param str
	 * @param index
	 * @return
	 */
	public static String getIndexUppercase(String str, int index) {
		return getIndexcase(str, index, IndexCaseTypes.UPPERCASE);
	}

	/**
	 * str 문자열에서 index에 해당하는 부분만 소문자로 고친다.
	 * 
	 * @param str
	 * @param index
	 * @return
	 */
	public static String getIndexLowercase(String str, int index) {
		return getIndexcase(str, index, IndexCaseTypes.LOWERCASE);
	}
}
