package org.access.googledata.multithreat;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import org.access.googledata.multithreat.Key.KEY_STATUS;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FontCharset;

import com.google.gson.JsonArray;

public class GoogleAutoSearch {

	public static Map<String, ArrayList<Result>> results = new HashMap<>();

	public static ArrayList<Keyword> keywords = new ArrayList<>();

	private static JFrame frame = new JFrame(
			"Google Maps Automatic Data Exporter");
	public static JProgressBar progress = new JProgressBar();
	private static ArrayList<JProgressBar> micro_progresses = new ArrayList<>();
	private static TitledBorder border = BorderFactory
			.createTitledBorder("Stating...");
	public static JButton quit = new JButton("Quit");

	public static ArrayList<Thread> threats = new ArrayList<Thread>();
	public static ArrayList<doSearch> runnables = new ArrayList<doSearch>();

	public static ArrayList<String> resume = new ArrayList<String>();

	private static final String header[] = { "Name", "Address", "Latitude",
			"Longitude", "Type", "Icon", "ID", "Place ID" };
	
	private static ArrayList<Key> keys = new ArrayList<>();

	enum Search_Type {
		By_Keywords, By_Types
	};

	public static Search_Type st = Search_Type.By_Keywords;

	public static TypeSearch types = null;

	public static void main(String args[]) {
		try {

			System.setProperty("file.encoding", "UTF-8");

			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Container content = frame.getContentPane();
			progress.setValue(0);
			progress.setStringPainted(true);
			progress.setBorder(border);
			content.add(progress, BorderLayout.NORTH);
			frame.setSize(1000, 1000);
			frame.setVisible(true);

			JPanel panel1 = new JPanel();
			JPanel panel2 = new JPanel();
			JPanel panel = new JPanel();
			panel.add(panel1/* ,BorderLayout.NORTH */);
			panel.add(new JLabel("\n\n"));
			panel.add(panel2/* ,BorderLayout.SOUTH */);
			JScrollPane spane = new JScrollPane(panel);
			spane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			
			JPanel quit_panel = new JPanel();
			quit_panel.add(quit);
			frame.add(quit_panel,BorderLayout.SOUTH);
			
			quit.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					GoogleAutoSearch.exit();
				}
			});

			JFileChooser in_file_chooser = new JFileChooser(
					System.getProperty("usr.home"));
			JFileChooser in_file_chooser1 = new JFileChooser(
					System.getProperty("usr.home"));
			JFileChooser in_file_chooser2 = new JFileChooser(
					System.getProperty("usr.home"));
			JFileChooser in_file_chooser3 = new JFileChooser(
					System.getProperty("usr.home"));
			JFileChooser in_file_chooser5 = new JFileChooser(
					System.getProperty("usr.home"));
			in_file_chooser.setDialogTitle("Select Zones File");
			int res = in_file_chooser.showOpenDialog(frame);

			if (res != JFileChooser.APPROVE_OPTION) {
				// JOptionPane.showMessageDialog(null, "An Error Acroud");
				throw new IOException("Input File Error");
			}
			int raduis;

			int max_search_count = 0;

			File zones_f = in_file_chooser.getSelectedFile();
			Scanner zones = new Scanner(zones_f);
			raduis = zones.nextInt();
			System.out.println("III");
			double slat, elat, slng, elng;
			while (zones.hasNextLine()) {
				zones.nextLine();
				slat = zones.nextDouble();
				zones.nextLine();
				System.out.println("III");
				slng = zones.nextDouble();
				zones.nextLine();
				System.out.println("III");
				elat = zones.nextDouble();
				zones.nextLine();
				System.out.println("III");
				elng = zones.nextDouble();
				System.out.println("III");
				Zone zone = new Zone(slat, slng, elat, elng, raduis);
				int max = zone.calculatePoints();
				TitledBorder border = BorderFactory.createTitledBorder(zone
						.toString());
				JProgressBar progress = new JProgressBar(0, max);
				progress.setValue(0);
				progress.setMaximum(max);
				progress.setStringPainted(true);
				progress.setBorder(border);
				doSearch searcher = new doSearch(zone, progress);
				Thread t = new Thread(searcher);
				threats.add(t);
				runnables.add(searcher);
				max_search_count += zone.calculatePoints();
				System.out.println(zone.calculatePoints());
				panel2.add(progress);
			}
			System.out.println("LINE 104 WORKS");

			String[] buttons00 = { "With Keywords", "With Types" };
			int searchtype = JOptionPane.showOptionDialog(null,
					"How Do You Wanna Search?", "There is Tow Types Of Search",
					JOptionPane.WARNING_MESSAGE, 0, null, buttons00,
					buttons00[0]);

			in_file_chooser1.setDialogTitle("Select Keywords File");

			res = in_file_chooser1.showOpenDialog(frame);

			if (res != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(null, "An Error Acroud");
				throw new IOException("Input File Error");
			}

			File keywords_file = in_file_chooser1.getSelectedFile();
			Scanner keywords_scanner = new Scanner(keywords_file, "UTF-8");
			Search_Type type = searchtype == 0 ? Search_Type.By_Keywords
					: Search_Type.By_Types;
			while (keywords_scanner.hasNextLine()) {
				String name = keywords_scanner.nextLine();
				TitledBorder border = BorderFactory.createTitledBorder(name);
				JProgressBar progress = new JProgressBar(0, max_search_count);
				progress.setValue(0);
				progress.setMaximum(max_search_count);
				progress.setStringPainted(true);
				progress.setBorder(border);
				GoogleAutoSearch.keywords.add(new Keyword(name,
						max_search_count, progress, type));
				panel1.add(progress);
			}

			
			in_file_chooser5.setDialogTitle("Select Key File");

			int res5 = in_file_chooser5.showOpenDialog(frame);
			
			File google_keys_file = in_file_chooser5.getSelectedFile();
			Scanner google_keys = new Scanner(google_keys_file, "UTF-8");
			while (google_keys.hasNextLine()) {
				String code = google_keys.nextLine();
				String st = google_keys.nextLine();
				try{
					int usage = Integer.parseInt(st);
					keys.add(new Key(code, usage));
				}catch (NumberFormatException e){
					if(st == "INVALID_KEY")
						keys.add(new Key(code, KEY_STATUS.INVALID_KEY));
					else if (st == "OVER_QUERY")
						keys.add(new Key(code, KEY_STATUS.OVER_QUERY));
				}
			}
			
			progress.setMaximum(max_search_count
					* GoogleAutoSearch.keywords.size());

			System.out.println("LINE 132 WORKS");

			in_file_chooser2.setDialogTitle("Select Output Excel File");
			System.out.println("LINE 133 WORKS");
			in_file_chooser2.setFileFilter(new FileFilter() {

				@Override
				public String getDescription() {
					// TODO Auto-generated method stub
					return "*.xls";
				}

				@Override
				public boolean accept(File f) {
					// TODO Auto-generated method stub
					if (f.getName().endsWith("xls"))
						return true;
					return false;
				}
			});
			System.out.println("LINE 132 WORKS");
			JDialog d2 = new JDialog();
			res = in_file_chooser2.showSaveDialog(d2);
			System.out.println("LINE 133 WORKS");

			if (res != JFileChooser.APPROVE_OPTION) {
				throw new IOException("Output File Error");
			}

			File file;
			System.out.println(in_file_chooser2.getSelectedFile().getPath());
			if (in_file_chooser2.getSelectedFile().getName().endsWith(".xls"))
				file = in_file_chooser2.getSelectedFile();
			else {
				in_file_chooser2.getSelectedFile().delete();
				file = new File(in_file_chooser2.getSelectedFile().getPath()
						+ ".xls");
			}
			if (file.exists())
				file.delete();
			file.createNewFile();
			// if(!file.createNewFile())return;
			FileOutputStream fos = new FileOutputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook();

			String[] buttons = { "Open Resume File", "It's a New Search" };
			int resume_count = JOptionPane.showOptionDialog(frame,
					"Do You Want To Resume Search ?", "Resume?",
					JOptionPane.WARNING_MESSAGE, 0, null, buttons, buttons[0]);

			if (resume_count == 0) {
				res = in_file_chooser3.showOpenDialog(frame);

				if (res != JFileChooser.APPROVE_OPTION) {
					throw new IOException("Output File Error");
				}

				File _file = in_file_chooser3.getSelectedFile();
				Scanner resume_scan = new Scanner(_file);

				// if (resume_scan.nextLine().startsWith("[res]") ||
				// resume_scan.nextLine().endsWith("[res]"))
				// throw new IOException("Wrong File");

				resume_scan.nextLine();
				double lat, lng;
				int i;
				System.out.println("LINE 225");
				for (doSearch thread : runnables) {
					lat = resume_scan.nextDouble();
					resume_scan.nextLine();
					lng = resume_scan.nextDouble();
					resume_scan.nextLine();
					i = resume_scan.nextInt();
					resume_scan.nextLine();

					thread.resume(new Point(lat, lng), i);
				}

			}

			// for (Runnable runnable : runnables) {
			// runnable.run();
			// }

			max_search_count *= keywords.size();

			content.add(spane, BorderLayout.CENTER);

			for (Thread t : threats) {
				t.start();
			}

			for (Thread t : threats) {
				t.join();
			}

			System.out.println("Exporting Data");
			Font font = workbook.createFont();
			font.setCharSet(FontCharset.ARABIC.getValue());
			font.setFontHeightInPoints((short) 11);
			font.setFontName("B Mitra");

			HSSFCellStyle cellStyleName = workbook.createCellStyle();
			HSSFCellStyle cellStyleH = workbook.createCellStyle();
			HSSFCellStyle cellStyleA = workbook.createCellStyle();
			HSSFCellStyle cellStyleB = workbook.createCellStyle();

			cellStyleName.setFillForegroundColor(HSSFColor.RED.index);
			cellStyleName.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			cellStyleName.setFont(font);
			cellStyleH
					.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
			cellStyleH.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			cellStyleA.setFillForegroundColor(HSSFColor.GOLD.index);
			cellStyleA.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			cellStyleA.setFont(font);
			cellStyleB.setFont(font);

			for (String key : results.keySet()) {
				System.out.println(key);
				int row_count = 0;
				HSSFSheet worksheet;
				worksheet = workbook.createSheet(new String(key));
				URL url;

				HSSFRow row01 = worksheet.createRow(row_count++);
				HSSFCell name_cell = row01.createCell(0);
				// name_cell.getStringCellValue().getBytes(Charset.forName("UTF-8"));
				String name = key;
				try {
					name = new String(key.getBytes(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				;
				name_cell.setCellValue(name);

				name_cell.setCellStyle(cellStyleName);
				// data.add(header);
				HSSFRow row1 = worksheet.createRow(row_count++);
				// String data_head[] = new String [8];
				int c = 0;
				for (String cellName : header) {
					// data_head[c] = cellName;
					HSSFCell header_cell = row1.createCell(c);
					header_cell.setCellValue(cellName);
					// header_cell.getStringCellValue().getBytes(Charset.forName("UTF-8"));
					header_cell.setCellStyle(cellStyleH);
					c++;
				}

				System.out.println(results.get(key).size());

				for (Result result : results.get(key)) {
					HSSFRow new_row;
					new_row = worksheet.createRow(row_count++);

					HSSFCell cellA = new_row.createCell(0);
					HSSFCell cellB = new_row.createCell(1);
					HSSFCell cellC = new_row.createCell(2);
					HSSFCell cellD = new_row.createCell(3);
					HSSFCell cellE = new_row.createCell(4);
					HSSFCell cellF = new_row.createCell(5);
					HSSFCell cellG = new_row.createCell(6);
					HSSFCell cellH = new_row.createCell(7);

					// Cell A
					cellA.setCellStyle(cellStyleA);
					cellA.setCellValue(result.getName());

					// Cell B
					cellB.setCellStyle(cellStyleB);
					cellB.setCellValue(result.getAddress());

					// Cell C..H
					cellC.setCellValue(result.getLatitude());
					cellD.setCellValue(result.getLongitude());
					cellE.setCellValue(result.getTypes());
					cellF.setCellValue(result.getIcon());
					cellG.setCellValue(result.getId());
					cellH.setCellValue(result.getPlace_id());
					
//					cellC.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
//					cellD.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				}
			}

			if (!resume.isEmpty()) {
				String[] buttons01 = { "Yes", "No" };
				int resume_save = JOptionPane.showOptionDialog(null,
						"Do You Want To Save Resume File",
						"Search Has Not Been Completed",
						JOptionPane.WARNING_MESSAGE, 0, null, buttons01,
						buttons01[0]);
				if (resume_save == 0) {
					JDialog d4 = new JDialog();
					JFileChooser in_file_chooser4 = new JFileChooser(
							System.getProperty("usr.home"));
					res = in_file_chooser4.showSaveDialog(d4);

					if (res != JFileChooser.APPROVE_OPTION) {
						throw new IOException("Output File Error");
					}

					File _file = in_file_chooser4.getSelectedFile();

					if (_file.exists())
						_file.delete();
					_file.createNewFile();
					PrintWriter writer = new PrintWriter(_file, "UTF-8");
					writer.println("[res]");
					for (String s : resume) {
						writer.println(s);
					}
					writer.close();
				}
			}

			workbook.write(fos);
			workbook.close();
			fos.close();
			keywords_scanner.close();
			zones.close();
			google_keys.close();
			google_keys_file.delete();
			google_keys_file.createNewFile();
			PrintStream google_keys_out = new PrintStream(google_keys_file);
			for (Key key : keys){
				google_keys_out.println(key.getKey());
				google_keys_out.println((key.getStats()==KEY_STATUS.OK ? key.getUsageCount() : key.getStats().toString()));
			}
			
			frame.setVisible(false);
			System.exit(0);
		} catch (Exception e) {
			JOptionPane.showConfirmDialog(null, e.getMessage(), "Error",
					JOptionPane.NO_OPTION, JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static String getKey() {
		// TODO Auto-generated method stub
		Key ret_key = new Key("NONE", 999);
		for (Key key : keys){
			if(key.isUsable())
				if(key.getUsageCount()<ret_key.getUsageCount()){
					ret_key= key;
				}
		}
		if(ret_key.getKey() == "NONE"){
			System.out.println("No Key Available Keys.Exiting...");
			JOptionPane.showConfirmDialog(null, "No Keys Available. Exiting ...");

			try {
				Thread.sleep(2000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			exit();
		}
		ret_key.use();
		return ret_key.getKey();
	}

	public static void OverQueryKey(String key) {
		// TODO Auto-generated method stub
		for(Key k : keys){
			if(k.getKey() == key){
				k.setOverQuery();
//				JOptionPane.showConfirmDialog(null, String.format("Key %s is now Unusable ( OVER_QUERY_LIMIT )",k.getKey()));
				break;
			}
		}
	}
	
	public static void InvalidKey(String key) {
		// TODO Auto-generated method stub
		for(Key k : keys){
			if(k.getKey() == key){
				k.setInvalid();
//				JOptionPane.showConfirmDialog(null, String.format("Key %s is Invalyd",k.getKey()));
				break;
			}
		}
	}
	
	public static void exit(){
		for (Thread t : threats) {
			t.stop();
		}
	}
}
