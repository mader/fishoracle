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

package de.unihamburg.zbh.fishoracle.server;

import java.util.Date;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.client.data.EnsemblGene;
import de.unihamburg.zbh.fishoracle.client.rpc.EnsemblService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;
import extended.RDBMysql;

public class EnsemblServiceImpl extends RemoteServiceServlet implements EnsemblService {

	private static final long	serialVersionUID	= 1L;
	
	/**
	 * Fetches gene data for a particular gene.
	 * 
	 * @param query The ensembl stable id
	 * 
	 * @return gene
	 * @throws Exception 
	 * */
	@Override
	public EnsemblGene fetchGeneForId(String stableId, String ensemblDB) throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		//replace with logger
		Date dt = new Date();
		System.out.println(dt + " Get gene data for: " + stableId);
		
		DBInterface db = new DBInterface(servletContext);
		
		RDBMysql rdb = db.getEnsemblRDB(ensemblDB);
		
		EnsemblGene  gene = db.getGeneInfos(rdb, stableId);
		
		rdb.delete();
		
		return gene;
	}
}