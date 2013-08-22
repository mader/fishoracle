/*
  Copyright (c) 2013 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2013 Center for Bioinformatics, University of Hamburg

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

import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.form.fields.SelectItem;

import de.unihamburg.zbh.fishoracle.client.data.FoConstants;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.ProjectDS;

public class ProjectSelectItem extends SelectItem {

	public ProjectSelectItem(String config) {
		super();
		this.setTitle("Project");
		if(config.equals(FoConstants.PROJECT_SELECT_MULTI)){
			this.setMultiple(true);
			this.setMultipleAppearance(MultipleAppearance.PICKLIST);
			this.setVisible(false);
		}
		this.setDisplayField("projectName");
		this.setValueField("projectId");
		this.setAutoFetchData(false);
		ProjectDS pDS = new ProjectDS();
		
		this.setOptionDataSource(pDS);
		this.setOptionOperationId(OperationId.PROJECT_FETCH_ALL);
		
		this.setDefaultToFirstOption(true);
	}
}
