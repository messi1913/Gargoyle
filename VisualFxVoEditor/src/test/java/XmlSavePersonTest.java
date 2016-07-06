import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Test;

import com.kyj.fx.voeditor.visual.util.SAXPasrerUtil;

/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   :
 *	작성일   : 2015. 12. 7.
 *	프로젝트 : Gagoyle
 *	작성자   : KYJ
 *******************************/

/**
 * @author KYJ
 *
 */
public class XmlSavePersonTest {

	@Test
	public void test() throws Exception {
		Person person = new Person();
		person.setName("김영준");
		person.setAge(13);

		List<Person> asList = Arrays.asList(person);
		// Wrapping our person data.
		PersonListWrapper wrapper = new PersonListWrapper();
		wrapper.setPersons(asList);

		SAXPasrerUtil.saveXml(new File("sample"), wrapper);

		PersonListWrapper loadXml = SAXPasrerUtil.loadXml(new File("sample"), PersonListWrapper.class);
		System.out.println(loadXml);
		// savePersonDataToFile(new File("sample"), asList);
	}

	// /**
	// * Saves the current person data to the specified file.
	// *
	// * @param file
	// */
	// public void savePersonDataToFile(File file, List<Person> personData) {
	// try {
	// JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
	// Marshaller m = context.createMarshaller();
	// m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	//
	// // Marshalling and saving XML to the file.
	// m.marshal(wrapper, file);
	//
	// } catch (Exception e) { // catches ANY exception
	// Alert alert = new Alert(AlertType.ERROR);
	// alert.setTitle("Error");
	// alert.setHeaderText("Could not save data");
	// alert.setContentText("Could not save data to file:\n" + file.getPath());
	//
	// alert.showAndWait();
	// }
	// }
}

@XmlRootElement(name = "persons")
class PersonListWrapper {

	private List<Person> persons;

	@XmlElement(name = "person")
	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}

	@Override
	public String toString() {
		return "PersonListWrapper [persons=" + persons + "]";
	}

}

class Person {
	private String name;
	private int age;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", age=" + age + "]";
	}

}