/*
  Copyright (c) 2014 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2014 Center for Bioinformatics, University of Hamburg

  Permission to use, copy, modify, and distribute this software for any
  purpose with or without fee is hereby granted, provided that the above
  copyright notice and this permission notice appear in all copies.

  THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
  WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
  ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
  WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
  ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
  OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

public class WelcomeTab extends Tab {

	public WelcomeTab(){
		
		this.setTitle("Welcome");
		VLayout welcomeLayout = new VLayout();
		welcomeLayout.setContents("" +
        		"<center><h1>FISH Oracle</h1></center>" +
        		"<p id=\"welcome\">You can search for genomic regions" +
        		" or for a gene specified by a gene name e.g. 'kras'" +
        		" or a karyoband." +
        		" By clicking on an element a window opens that shows additional information." +
        		"</p>" +
        		"<p id=\"welcome\">" +
        		"For more information how to use the FISH Oracle read the user " +
        		"<a href=\"http://eppendorf.zbh.uni-hamburg.de:9999/manual.pdf\" target=_blank>manual</a> or" +
        		"watch the introduction tutorial on " +
        		"<a href=\"http://www.youtube.com/channel/UC-98INTGbA0E4h6nDjJDe7w\" target=_blank>YouTube</a> " +
        		"</p>" +
        		"<p id=\"welcome\"><br>FISH Oracle uses:<br> " +
        		"<ul id=\"welcome\">" +
        		"<li> the Google Web Toolkit <a href=\"http://www.gwtproject.org/\" target=_blank>http://www.gwtproject.org/</a></li>" +
        		"<li> the SmartGWT <a href=\"http://code.google.com/p/smartgwt/\" target=_blank>http://code.google.com/p/smartgwt/</a></li>" +
        		"<li> the Ensembl human core database <a href=\"http://www.ensembl.org\" target=_blank>http://www.ensembl.org</a></li>" +
        		"<li> AnnotationSketch of the GenomeTools <a href=\"http://www.genometools.org\" target=_blank>http://www.genometools.org</a></li>" +
        		"</ul></p>");
		
		this.setPane(welcomeLayout);	
	}
}