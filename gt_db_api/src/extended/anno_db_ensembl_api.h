/*
  Copyright (c) 2012 Malte Mader <mader@zbh.uni-hamburg.de>
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

#ifndef ANNO_DB_ENSEMBL_API_H
#define ANNO_DB_ENSEMBL_API_H

/* Implements the <GtAnnoDBSchema> interface. */
typedef struct GtAnnoDBEnsembl GtAnnoDBEnsembl;

typedef struct GtFeatureIndexEnsembl GtFeatureIndexEnsembl;

#include "core/error_api.h"
#include "extended/feature_index_rep.h"
#include "extended/feature_index.h"
#include "extended/feature_visitor.h"
#include "extended/anno_db_schema_api.h"

GtAnnoDBSchema* gt_anno_db_ensembl_new(void);

GtRDB* gt_feature_index_ensembl_get_db(GtFeatureIndex *gfi);
int gt_anno_db_feature_index_get_version(GtFeatureIndex *gfi, GtError *err);

GtStr* get_overlap_where_clause(const char *seqid,
                                unsigned long start,
                                unsigned long end);
#include "core/error_api.h"
#include "extended/anno_db_schema_api.h"

GtAnnoDBSchema* gt_anno_db_ensembl_new(void);
int gt_feature_index_ensembl_get_feature_for_gene_name(GtFeatureIndex *gfi,
                                                    GtGenomeNode **gn,
                                                    const char *gene_name,
                                                    GtError *err);
int gt_feature_index_ensembl_get_feature_for_stable_id(GtFeatureIndex *gfi,
                                                    GtGenomeNode **gn,
                                                    const char *stable_id,
                                                    GtError *err);
int gt_feature_index_ensembl_get_range_for_karyoband(GtFeatureIndex *gfi,
                                                    GtRange *range,
                                                    const char *chr,
                                                    const char *band,
                                                    GtError *err);
int gt_feature_index_ensembl_get_karyoband_features_for_range(GtFeatureIndex *gfi,
                                                    GtArray *results,
                                                    const char *seqid,
                                                    GtRange *qry_range,
                                                    GtError *err);
int             gt_anno_db_ensembl_unit_test(GtError *err);

#endif
