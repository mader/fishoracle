/*
<<<<<<< HEAD
  Copyright (c) 2012 Malte Mader <mader@zbh.uni-hamburg.de>
=======
  Copyright (c) 2012 Sascha Steinbiss <steinbiss@zbh.uni-hamburg.de>
>>>>>>> Add database interfaces.
  Copyright (c) 2012 Center for Bioinformatics, University of Hamburg

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

#include <string.h>
#include "core/assert_api.h"
#include "extended/anno_db_ensembl_api.h"
#include "extended/anno_db_prepared_stmt.h"
#include "extended/anno_db_schema_rep.h"
#include "extended/rdb_visitor_rep.h"
#include "extended/feature_index_rep.h"
#include "extended/feature_index.h"
#include "extended/feature_visitor.h"
#include "core/cstr_api.h"
#include "core/ensure.h"
#include "core/fileutils_api.h"
#include "core/hashtable.h"
#include "core/hashmap.h"
#include "core/hashmap-generic.h"
#include "core/log_api.h"
#include "core/ma.h"
#include "core/range.h"
#include "core/strand_api.h"
#include "core/thread_api.h"
#include "core/undef_api.h"
#include "core/unused_api.h"
#include "core/fa.h"
#include "core/xansi_api.h"
#include "core/xposix.h"
#include "extended/genome_node.h"
#include "extended/gff3_in_stream.h"
#include "extended/feature_node.h"
#include "extended/feature_node_observer.h"
#include "extended/feature_node_iterator_api.h"
#include "extended/rdb_api.h"
#include "extended/rdb_sqlite_api.h"

struct GtAnnoDBEnsembl {
  const GtAnnoDBSchema parent_instance;
  GtRDB *db;
  GtRDBVisitor *visitor;
};

typedef struct {
  const GtRDBVisitor parent_instance;
  GtAnnoDBEnsembl *annodb;
} EnsemblSetupVisitor;

struct GtFeatureIndexEnsembl {
  const GtFeatureIndex parent_instance;
  GtRDB *db;
  int ensembl_version;
  GtMutex *dblock;
  bool transaction_lock;
};

#define anno_db_ensembl_cast(V)\
        gt_anno_db_schema_cast(gt_anno_db_ensembl_class(), V)

#define ensembl_setup_visitor_cast(V)\
        gt_rdb_visitor_cast(ensembl_setup_visitor_class(), V)

#define feature_index_ensembl_cast(V)\
        gt_feature_index_cast(feature_index_ensembl_class(), V)

const GtAnnoDBSchemaClass* gt_anno_db_ensembl_class(void);
static const GtRDBVisitorClass* ensembl_setup_visitor_class(void);
static const GtFeatureIndexClass* feature_index_ensembl_class(void);

void anno_db_ensembl_free(GtAnnoDBSchema *s)
{
  GtAnnoDBEnsembl *ade = anno_db_ensembl_cast(s);
  gt_rdb_visitor_delete(ade->visitor);
}

GtRDB* gt_feature_index_ensembl_get_db(GtFeatureIndex *gfi)
{
  GtFeatureIndexEnsembl *fi;
  fi = feature_index_ensembl_cast(gfi);
  return fi->db;
}

int gt_feature_index_ensembl_add_region_node()
{
  return 0;
}

int gt_feature_index_ensembl_add_feature_node()
{
  return 0;
}

int gt_feature_index_ensembl_remove_node()
{
  return 0;
}

GtArray* gt_feature_index_ensembl_get_features_for_seqid()
{
  return NULL;
}

int gt_feature_index_ensembl_get_features_for_range()
{
  return 0;
}

char* gt_feature_index_ensembl_get_first_seqid()
{
  return NULL;
}

GtStrArray* gt_feature_index_ensembl_get_seqids()
{
  return NULL;
}

int gt_feature_index_ensembl_get_range_for_seqid()
{
  return 0;
}

int gt_feature_index_ensembl_has_seqid()
{
  return 0;
}

int gt_feature_index_ensembl_save()
{
  return 0;
}

GtStr* get_overlap_where_clause(const char *seqid,
                                unsigned long start,
                                unsigned long end)
{
		GtStr *qry = gt_str_new();
		
		gt_str_append_cstr(qry, " WHERE seq_region.name = '");
        gt_str_append_cstr(qry, seqid);
        gt_str_append_cstr(qry, "'");
		
		gt_str_append_cstr(qry, " AND ((seq_region_start <= ");
		gt_str_append_ulong(qry, start);
		
		gt_str_append_cstr(qry, " AND seq_region_end <= ");
		gt_str_append_ulong(qry, end);
		
		gt_str_append_cstr(qry, " AND seq_region_end >= ");
		gt_str_append_ulong(qry, start);
		gt_str_append_cstr(qry, ")");
		
		gt_str_append_cstr(qry, " OR ");
		
		gt_str_append_cstr(qry, "( seq_region_start >= ");
		gt_str_append_ulong(qry, start);
		
		gt_str_append_cstr(qry, " AND seq_region_start <= " );
		gt_str_append_ulong(qry, end);
		
		gt_str_append_cstr(qry, " AND seq_region_end >= " );
		gt_str_append_ulong(qry, end);
		gt_str_append_cstr(qry, ")");
		
		gt_str_append_cstr(qry, " OR ");
		
		gt_str_append_cstr(qry, "(seq_region_start >= ");
		gt_str_append_ulong(qry, start);
		
		gt_str_append_cstr(qry, " AND seq_region_end <= ");
		gt_str_append_ulong(qry, end);
		gt_str_append_cstr(qry, ")");
		
		gt_str_append_cstr(qry, " OR ");
		
		gt_str_append_cstr(qry, "(seq_region_start <= ");
		gt_str_append_ulong(qry, start);
		
		gt_str_append_cstr(qry, " AND seq_region_end >= ");
		gt_str_append_ulong(qry, end);
		gt_str_append_cstr(qry, "))");
	    
		return qry;
}

int gt_anno_db_feature_index_get_version(GtFeatureIndex *gfi, GtError *err)
{
  gt_assert(gfi);
  GtFeatureIndexEnsembl *fi;
  fi = feature_index_ensembl_cast(gfi);
  
  gt_error_check(err);
  
  return fi->ensembl_version;
}

int gt_anno_db_ensembl_fetch_version(GtFeatureIndex *gfi, GtError *err)
{
  int had_err = 0;
  GtRDBStmt *stmt;
  gt_assert(gfi);
  gt_error_check(err);
  
  GtRDB* db = gt_feature_index_ensembl_get_db(gfi);
  
  GtStr *qry = gt_str_new();
  
  gt_str_append_cstr(qry, "SELECT meta_value"
                          " FROM meta"
                          " WHERE meta_key = 'schema_version'");
  
  stmt = gt_rdb_prepare(db,
                        gt_str_get(qry),
                        0,
                        err);
  
  gt_rdb_stmt_reset(stmt, err);
  
  GtStr *str_version = gt_str_new();
  int version = 0;
  
  if (!had_err && gt_rdb_stmt_exec(stmt, err) == 0) {
	
    gt_rdb_stmt_get_string(stmt, 0, str_version, err);
     
  }
  
  version = atoi(gt_str_get(str_version));
  
  gt_str_delete(str_version);
  gt_str_delete(qry);
  gt_rdb_stmt_delete(stmt);
  
  return version;
}

void gt_feature_index_ensembl_delete(GtFeatureIndex *gfi)
{
  GtFeatureIndexEnsembl *fi;
  if (!gfi) return;
  fi = feature_index_ensembl_cast(gfi);
  if (fi->db)
    gt_rdb_delete(fi->db);
  gt_mutex_delete(fi->dblock);
}

const GtFeatureIndexClass* feature_index_ensembl_class(void)
{
  static const GtFeatureIndexClass *fic = NULL;
  if (!fic) {
    fic = gt_feature_index_class_new(sizeof (GtFeatureIndexEnsembl),
                                gt_feature_index_ensembl_add_region_node,
                                gt_feature_index_ensembl_add_feature_node,
                                gt_feature_index_ensembl_remove_node,
                                gt_feature_index_ensembl_get_features_for_seqid,
                                gt_feature_index_ensembl_get_features_for_range,
                                gt_feature_index_ensembl_get_first_seqid,
                                gt_feature_index_ensembl_save,
                                gt_feature_index_ensembl_get_seqids,
                                gt_feature_index_ensembl_get_range_for_seqid,
                                gt_feature_index_ensembl_has_seqid,
                                gt_feature_index_ensembl_delete);
  }
  return fic;
}

GtFeatureIndex* anno_db_ensembl_build(GtAnnoDBSchema *schema, GtRDB *db,
                                      GtError *err)
{
  int had_err = 0;
  GtFeatureIndex *fi = NULL;
  GtFeatureIndexEnsembl *fis;
  GtAnnoDBEnsembl *ade;
  
  int ensembl_version;
  gt_assert(db);
  gt_assert(schema);
  gt_error_check(err);

  ade = anno_db_ensembl_cast(schema);
  had_err = gt_rdb_accept(db, ade->visitor, err);

  if (!had_err) {
    fi = gt_feature_index_create(feature_index_ensembl_class());
    fis = feature_index_ensembl_cast(fi);

    fis->db = gt_rdb_ref(db);

    ensembl_version = gt_anno_db_ensembl_fetch_version(fi, err);
    
    fis->ensembl_version = ensembl_version;
  }
  return fi;
}

const GtAnnoDBSchemaClass* gt_anno_db_ensembl_class()
{
  static const GtAnnoDBSchemaClass *adbsc = NULL;
  if (!adbsc) {
    adbsc = gt_anno_db_schema_class_new(sizeof (GtAnnoDBEnsembl),
                                        anno_db_ensembl_free,
                                        anno_db_ensembl_build);
  }
  return adbsc;
}

static const GtRDBVisitorClass* ensembl_setup_visitor_class()
{
  static const GtRDBVisitorClass *svc = NULL;
  if (!svc) {
    svc = gt_rdb_visitor_class_new(sizeof (GtAnnoDBEnsembl),
                                   NULL,
                                   NULL,
                                   NULL);
  }
  return svc;
}

static GtRDBVisitor* ensembl_setup_visitor_new(GtAnnoDBEnsembl *adb)
{
  GtRDBVisitor *v = gt_rdb_visitor_create(ensembl_setup_visitor_class());
  EnsemblSetupVisitor *sv = ensembl_setup_visitor_cast(v);
  gt_assert(adb);
  sv->annodb = adb;
  return v;
}

GtAnnoDBSchema* gt_anno_db_ensembl_new(void)
{
  GtAnnoDBSchema *s = gt_anno_db_schema_create(gt_anno_db_ensembl_class());
  GtAnnoDBEnsembl *ade = anno_db_ensembl_cast(s);
  ade->visitor = ensembl_setup_visitor_new(ade);
  return s;
}

int gt_anno_db_ensembl_unit_test(GtError *err)
{
  int had_err = 0;
  err = NULL;
  return had_err;
}
