/*
  Copyright (c) 2012-2013 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2012-2013 Center for Bioinformatics, University of Hamburg

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

package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;

import de.unihamburg.zbh.fishoracle.client.data.FoGenericFeature;

public interface FeatureService extends RemoteService {

	//FoGenericFeature add(FoGenericFeature foFeature);
	FoGenericFeature[] fetch(int studyId) throws Exception;
	//public void update(FoGenericFeature fiFeature);
	//public void delete(int featureId);
	String[] fetchTypes() throws Exception;
	
	public static class Util {

		public static FeatureServiceAsync getInstance() {

			return GWT.create(SegmentService.class);
		}
	}
	
}
