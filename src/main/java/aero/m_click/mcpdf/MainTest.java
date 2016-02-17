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
	public void testWatermark() {
		try {
			String[] args = {"/Users/mserrano/Desktop/sample.pdf", 
				"watermark", "-", 
				"text", "THIS IS A \r\nDRAFT!", 
				"fontsize", "48",
				"fontcolor", "0,191,255",
				"fontrotation", "90",
				"output", "-"};
			Main.main(args);
		} catch (Exception e) {
			System.err.println(e);
			fail(e.getMessage());
		}
	}
}
