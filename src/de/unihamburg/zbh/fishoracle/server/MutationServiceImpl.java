package de.unihamburg.zbh.fishoracle.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import core.GTerrorJava;

import de.unihamburg.zbh.fishoracle.client.data.EnsemblGene;
import de.unihamburg.zbh.fishoracle.client.data.FoConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoSNPMutation;
import de.unihamburg.zbh.fishoracle.client.data.FoTrackData;
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
	public FoSNPMutation[] fetchForConfig(String geneId, String ensemblId, FoTrackData cd) {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		RDBMysql rdb;
		EnsemblGene gene = null;
		
		try {
			rdb = db.getEnsemblRDB(ensemblId);
			gene = db.getGeneInfos(rdb, geneId);
		
			rdb.delete();
		} catch (GTerrorJava e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Location loc = new Location(gene.getChr(), gene.getStart(), gene.getEnd());
		
		return db.getMutationsForConfig(loc, cd);
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
