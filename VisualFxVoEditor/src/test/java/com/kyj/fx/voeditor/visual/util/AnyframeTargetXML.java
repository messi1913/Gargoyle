package com.kyj.fx.voeditor.visual.util;

/********************************
 *	프로젝트 : dim.mk.tree
 *	패키지   : com.kyj.dim.mk.tree.app
 *	작성일   : 2015. 12. 7.
 *	프로젝트 : Gagoyle
 *	작성자   : KYJ
 *******************************/

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author KYJ
 *
 */
public class AnyframeTargetXML {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnyframeTargetXML.class);

	@Test
	public void writeTest() throws JAXBException {
		Beans beans = new Beans();
		Job job = new Job();
		job.setId("sample");
		job.setName("haha");
		job.setDescription("테스트입니다.");
		beans.setJobs(Arrays.asList(job));
		Step step = new Step();
		step.setId("id0");
		step.setName("스텝0");
		step.setType("JAVA");
		step.setDescription("스텝0 desc");
		job.setSteps(Arrays.asList(step));
		step.setClazz("java.lang.Object");
		Resource resource = new Resource();
		Reader reader = new Reader();
		reader.setId("reader");
		reader.setType("DB");
		reader.setUrl("DS_DEFAULT");

		Writer writer = new Writer();
		writer.setId("writer");
		writer.setType("DB");
		writer.setUrl("DS_DEFAULT");

		resource.setReader(reader);
		resource.setWriter(writer);
		step.setResources(Arrays.asList(resource));
		writeXML("sample.xml", beans);
	}

	@Test
	public void loadTest() throws Exception {

		Beans loadAnyframeBatch = loadAnyframeBatch(new File("sample.xml"));


		System.out.println(loadAnyframeBatch);

	}

	public void writeXML(String filePathName, Beans xmlModel) throws JAXBException {
		saveAnyframeBatch(new File(filePathName), xmlModel);
	}

	/**
	 * Saves the current person data to the specified file.
	 *
	 * @param file
	 * @throws JAXBException
	 */
	public void saveAnyframeBatch(File file, Beans jobs) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Beans.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(jobs, file);
	}

	public Beans loadAnyframeBatch(File file) throws Exception {
		JAXBContext context = JAXBContext.newInstance(Beans.class);
		Unmarshaller um = context.createUnmarshaller();

		// Reading XML from the file and unmarshalling.
		return (Beans) um.unmarshal(file);
	}

}

@XmlRootElement(name = "beans")
class Beans {

	private List<Job> jobs;

	@XmlElement(name="job")
	public List<Job> getJobs() {
		return jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Beans [jobs=" + jobs + "]";
	}

}

@XmlRootElement(name = "job")
class Job {

	private String id;

	private String name;
	private String description;
	private List<Step> steps;

	@XmlAttribute
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElement(name="step")
	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Job [id=" + id + ", name=" + name + ", description=" + description + ", steps=" + steps + "]";
	}

}

@XmlRootElement(name = "step")
class Step {
	private String id;
	private String name;
	private String type;

	private String clazz;
	private String description;

	private List<Resource> resources;

	@XmlAttribute
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlAttribute(name = "class")
	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Step [id=" + id + ", name=" + name + ", type=" + type + ", clazz=" + clazz + ", description=" + description + ", resources="
				+ resources + "]";
	}

}

@XmlRootElement(name = "resources")
class Resource {
	private Reader reader;
	private Writer writer;

	public Reader getReader() {
		return reader;
	}

	public void setReader(Reader reader) {
		this.reader = reader;
	}

	public Writer getWriter() {
		return writer;
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Resource [reader=" + reader + ", writer=" + writer + "]";
	}

}

abstract class ReaderWriter {

	private String id;

	private String type;

	private String url;

	@XmlAttribute
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlAttribute
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ReaderWriter [id=" + id + ", type=" + type + ", url=" + url + "]";
	}

}

@XmlRootElement(name = "reader")
class Reader extends ReaderWriter {

}

@XmlRootElement(name = "writer")
class Writer extends ReaderWriter {

}