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

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;

public class ImageFrameResizedHandler implements ResizedHandler {

	private CenterPanel cp;
	
	public ImageFrameResizedHandler(CenterPanel centerPanel){
		cp = centerPanel;
	}
	
	@Override
	public void onResized(ResizedEvent event) {
		if(cp.getCenterTabSet().getTabs().length > 1){
			Canvas[] tabContents = cp.getCenterTabSet().getSelectedTab().getPane().getChildren();
			Canvas presentationLayer = cp.getCenterTabSet().getSelectedTab().getPane();
			ImgCanvas imgLayer = (ImgCanvas) tabContents[1];
			imgLayer.getImageInfo().setWidth(presentationLayer.getInnerWidth());
			cp.imageRedraw(imgLayer.getImageInfo());
		}
	}	
}