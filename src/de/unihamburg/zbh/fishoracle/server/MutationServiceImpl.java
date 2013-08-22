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

package de.unihamburg.zbh.fishoracle.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import core.GTerrorJava;

import de.unihamburg.zbh.fishoracle.client.data.EnsemblGene;
import de.unihamburg.zbh.fishoracle.client.data.FoConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoSNPMutation;
import de.unihamburg.zbh.fishoracle.client.rpc.MutationService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;
import de.unihamburg.zbh.fishoracle_db_api.data.Constants;
import de.unihamburg.zbh.fishoracle_db_api.data.Location;
import extended.RDBMysql;

public class MutationServiceImpl extends RemoteServiceServlet implements MutationService {

	private static final long	serialVersionUID	= 1L;

	@Override
	public FoSNPMutation add(FoSNPMutation foMutation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FoSNPMutation[] fetch(int studyId) throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getMutationsForStudyId(studyId);
	}

	@Override
	public FoSNPMutation[] fetchForConfig(String geneId, int trackId, FoConfigData cd) {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		RDBMysql rdb;
		EnsemblGene gene = null;
		
		try {
			rdb = db.getEnsemblRDB(cd.getStrArray(Constants.ENSEMBL_ID)[0]);
			gene = db.getGeneInfos(rdb, geneId);
		
			rdb.delete();
		} catch (GTerrorJava e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Location loc = null;
		try {
			loc = new Location(gene.getChr(), gene.getStart(), gene.getEnd());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return db.getMutationsForConfig(loc, trackId, cd);
	}
	
	@Override
	public void update(FoSNPMutation segment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int segmentId) {
		// TODO Auto-generated method stub
		
	}
}
