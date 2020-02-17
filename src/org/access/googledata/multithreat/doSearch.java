package org.access.googledata.multithreat;

import java.io.IOException;

import javax.swing.JProgressBar;

import org.access.googledata.multithreat.Keyword.STATUS;

public class doSearch implements Runnable {

	private final Zone zone;
	private JProgressBar progress;

	private boolean res = false;
	private int index_res = 0;

	public doSearch(Zone zone, JProgressBar progress) {
		this.zone = zone;
		this.progress = progress;
	}

	public void resume(Point p, int i) {
		index_res = i;
		zone.resume(p);
		res = true;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Location loc = zone.getFirst();
		if (res) {
			for (int i = index_res; i < index_res; i++) {
				try {
					Keyword key = GoogleAutoSearch.keywords.get(i);
					STATUS ret = key.search(loc);
					if (ret == STATUS.OVER_QUERY_LIMIT) {
						String s = String.format("%.6f\n%.6f\n%d",
								loc.getLat(), loc.getLng(),
								GoogleAutoSearch.keywords.indexOf(key));
						GoogleAutoSearch.resume.add(s);
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			loc = zone.next();
		}
		do {
			System.out.println(loc.toString());
			for (Keyword key : GoogleAutoSearch.keywords) {
				try {
					STATUS ret = key.search(loc);
					if (ret == STATUS.OVER_QUERY_LIMIT) {
						String s = String.format("%.6f\n%.6f\n%d",
								loc.getLat(), loc.getLng(),
								GoogleAutoSearch.keywords.indexOf(key));
						GoogleAutoSearch.resume.add(s);
						return;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			progress.setValue(progress.getValue() + 1);
		} while ((loc = zone.next()) != null);
		System.out.println("Thread Exited");
	}
}
