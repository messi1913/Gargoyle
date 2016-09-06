package simple;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Test;

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
public class XmlSaveTreeModelTest {

	@Test
	public void test() {
		DimmensionXmlModel person = new DimmensionXmlModel();
		person.setName("루트");
		{
			List<DimmensionXmlModel> arraylist = new ArrayList<DimmensionXmlModel>();
			{
				DimmensionXmlModel e = new DimmensionXmlModel();
				e.setName("자식1");
				arraylist.add(e);
			}

			{
				DimmensionXmlModel e = new DimmensionXmlModel();
				e.setName("자식2");
				arraylist.add(e);
			}
			person.setChildrens(arraylist);
		}

		savePersonDataToFile(new File("sample"), Arrays.asList(person));
	}
	/**
	 * Saves the current person data to the specified file.
	 * 
	 * @param file
	 */
	public void savePersonDataToFile(File file, List<DimmensionXmlModel> personData) {
		try {
			JAXBContext context = JAXBContext.newInstance(DimensionWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Wrapping our person data.
			DimensionWrapper wrapper = new DimensionWrapper();
			wrapper.setPersons(personData);

			// Marshalling and saving XML to the file.
			m.marshal(wrapper, file);

		} catch (Exception e) { // catches ANY exception
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not save data");
			alert.setContentText("Could not save data to file:\n" + file.getPath());

			alert.showAndWait();
		}
	}
}

@XmlRootElement(name = "dimensionDirs")
class DimensionWrapper {

	private List<DimmensionXmlModel> persons;

	@XmlElement(name = "dimensionDir")
	public List<DimmensionXmlModel> getPersons() {
		return persons;
	}

	public void setPersons(List<DimmensionXmlModel> persons) {
		this.persons = persons;
	}
}

class DimmensionXmlModel {

	private List<DimmensionXmlModel> childrens;

	private String name;

	public DimmensionXmlModel() {
	}

	/**
	 * @return the childrens
	 */
	public List<DimmensionXmlModel> getChildrens() {
		return childrens;
	}

	/**
	 * @param childrens
	 *            the childrens to set
	 */
	public void setChildrens(List<DimmensionXmlModel> childrens) {
		this.childrens = childrens;
	}

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

}