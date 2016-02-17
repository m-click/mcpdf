/**
 * Mcpdf is a drop-in replacement for PDFtk.
 *
 * It fixes PDFtk's unicode issues when filling in PDF forms,
 * and is essentially a command line interface for the iText
 * PDF library with a PDFtk compatible syntax.
 */

/*
 * Copyright (C) 2014  Volker Grabsch <grabsch@m-click.aero>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * https://www.gnu.org/licenses/agpl-3.0.html
 */

package aero.m_click.mcpdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.FdfReader;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.XfdfReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.util.Arrays;

public class Main
{
    public static void main(String[] args)
    {
        try {
        	if (isWatermark(args)) {
        		watermark(parseArgs(args));
        	} else {
        		execute(parseArgs(args));
        	}
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("See README for more information.");
            System.exit(1);
        }
    }

    public static Config parseArgs(String[] args)
            throws FileNotFoundException
    {
        if (args.length == 0) {
            throw new RuntimeException("Missing arguments.");
        }
        Config config = new Config();
        config.pdfInputStream = new FileInputStream(args[0]);
        config.pdfOutputStream = System.out;
        config.formInputStream = null;
        config.flatten = false;
        config.watermark = false;
        config.text = "DRAFT";
        config.font_color = BaseColor.RED;
        config.font_rotation = 45;
        config.font_size = 24;
        config.isFdf = false;
        byte[] fdfHeader = "%FDF".getBytes();
        for (int i = 1; i < args.length; i++) {
        	if ("fill_form".equals(args[i]) || 
        			"watermark".equals(args[i])) {
                config.formInputStream = new BufferedInputStream(System.in);
                config.formInputStream.mark(4);
                try {
                    byte[] header = new byte[4];
                    config.formInputStream.read(header, 0, 4);
                    if (Arrays.equals(header, fdfHeader)) {
                        config.isFdf = true;
                    }
                    config.formInputStream.reset();
                } catch (Exception e) {
                    System.err.println(e);
                    System.err.println("Problem reading standard input.");
                }
                i++;
                if (!"-".equals(args[i])) {
                    throw new RuntimeException("Missing \"-\" after fill_form operation.");
                }
            } else if ("text".equals(args[i])) {
            	i++;
            	config.text = args[i].toUpperCase();
            } else if ("fontsize".equals(args[i])) {
            	i++;
            	try {
            		config.font_size = Integer.parseInt(args[i]);
            	} catch (NumberFormatException n) {
            		throw new RuntimeException("Invalid font size, must be an integer.");
            	}
            } else if ("fontcolor".equals(args[i])) {
            	i++;
            	int[] rgb_int = rgb(args[i]);
            	if (rgb_int == null) {
            		throw new RuntimeException ("Invalid RGB. RGB must be in the form "
            				+ "red,green,blue with no spaces and commas in between.");
            	}
            	config.font_color = new BaseColor(rgb_int[0], rgb_int[1], rgb_int[2]);
            } else if ("fontrotation".equals(args[i])) {
            	i++;
            	try {
            		config.font_rotation = Integer.parseInt(args[i]);
            	} catch(NumberFormatException n) {
            		throw new RuntimeException("Invalid font rotation, must be an integer.");
            	}
            }
        	else if ("output".equals(args[i])) {
                i++;
                if (!"-".equals(args[i])) {
                    throw new RuntimeException("Missing \"-\" after output operation.");
                }
            }  else if ("flatten".equals(args[i])) {
                config.flatten = true;
            } 
            else {
                throw new RuntimeException("Unknown operation: " + args[i]);
            }
        }
        return config;
    }

    public static void execute(Config config)
            throws IOException, DocumentException
    {
        PdfReader reader = new PdfReader(config.pdfInputStream);
        PdfStamper stamper = new PdfStamper(reader, config.pdfOutputStream, '\0');
        if (config.formInputStream != null) {
            if (config.isFdf) {
                stamper.getAcroFields().setFields(new FdfReader(config.formInputStream));
            } else {
                stamper.getAcroFields().setFields(new XfdfReader(config.formInputStream));
            }
            
        }
        stamper.setFormFlattening(config.flatten);
        stamper.close();
        reader.close();
    }
    
    public static void watermark(Config config) 
    	throws IOException, DocumentException {
    		PdfReader reader = new PdfReader(config.pdfInputStream);
    		PdfStamper stamper = new PdfStamper(reader, config.pdfOutputStream, '\0');
    		int pages = reader.getNumberOfPages();
    		Font f = new Font(FontFamily.HELVETICA, config.font_size);
    		f.setColor(config.font_color);
    		f.setStyle(Font.BOLD);
    		Phrase p = new Phrase(config.text, f);
    		PdfGState gsl = new PdfGState();
    		gsl.setFillOpacity(0.3f);
    		PdfContentByte over;
    		Rectangle pagesize;
    		float x, y;
    		for (int i = 1; i <= pages; i++) {
    			pagesize = reader.getPageSizeWithRotation(i);
    			x = (pagesize.getLeft() + pagesize.getRight()) / 2;
    			y = (pagesize.getTop() + pagesize.getBottom()) / 2;
    			over = stamper.getOverContent(i);
    			over.saveState();
    			over.setGState(gsl);
    			ColumnText.showTextAligned(over, Element.ALIGN_CENTER, 
    					p, x, y, config.font_rotation);
    			over.restoreState();
    		}
    		stamper.close();
    		reader.close();
    	}
    
    public static boolean isWatermark(String[] args) {
    	for (int i = 1;i < args.length; i++) {
    		if ("watermark".equals(args[i])) {
    			return true;
   			}
   		}
   		return false;
   	}
    
    public static int[] rgb(String rgb) {
    	String[] rgb_split = rgb.split(",");
    	if (rgb_split.length == 0 || rgb_split.length != 3) {
    		return null;
    	}
    	int[] rgb_int = new int[3];
    	for (int i = 0; i < rgb_split.length; i++) {
    		rgb_int[i] = Integer.parseInt(rgb_split[i]);
    	}
    	return rgb_int;
    }
}

