package aero.m_click.mcpdf;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

public class MainTest {
	FileInputStream i;
	PrintStream o;
	@Before 
	public void initialize() {
		try {
			i = new FileInputStream("/Users/mserrano/Desktop/sample.fdf");
			o = new PrintStream("/Users/mserrano/Desktop/result.pdf");
			System.setIn(i);
			System.setOut(o);
		} catch(Exception e) {
			System.err.println(e);
		}
	}
	
	@Test
	public void testPDF() { 
		try {
			String[] args = {"/Users/mserrano/Desktop/sample.pdf", 
				"fill_form", "-", "output", "-"}; 
			Main.main(args);
		 } catch (Exception e) {
			 System.err.println(e);
			 fail(e.getMessage());
		 }
	}
	
	@Test
	public void testBackground() {
		try {
			String[] args = {"/Users/mserrano/Desktop/sample.pdf", 
				"background", "/Users/mserrano/Desktop/draft.pdf", 
				"output", "-"};
			Main.main(args);
		} catch (Exception e) {
			System.err.println(e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testStamp() {
		try {
			String[] args = {"/Users/mserrano/Desktop/sample.pdf", 
				"stamp", "/Users/mserrano/Desktop/draft.pdf", 
				"output", "/Users/mserrano/Desktop/result.pdf"};
			Main.main(args);
		} catch (Exception e) {
			System.err.println(e);
			fail(e.getMessage());
		}
	}
}
