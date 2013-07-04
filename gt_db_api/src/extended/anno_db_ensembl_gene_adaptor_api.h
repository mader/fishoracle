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

#ifndef ENSEMBL_GENE_ADAPTOR_API_H
#define ENSEMBL_GENE_ADAPTOR_API_H

typedef struct GtEnsemblGeneAdaptor GtEnsemblGeneAdaptor;

#include "core/error_api.h"
#include "extended/feature_index_api.h"
#include "extended/anno_db_ensembl_api.h"
#include "annotationsketch/feature_collection.h"
#include "extended/feature_node_api.h"
#include "core/array_api.h"


GtEnsemblGeneAdaptor* gt_ensembl_gene_adaptor_new(int ensembl_version);
int gt_ensembl_fetch_gene_for_symbol(GtEnsemblGeneAdaptor *ga,
                                     GtFeatureIndex *gfi,
                                     GtGenomeNode **gn,
                                     const char *gene_name,
                                     GtError *err);
int gt_ensembl_fetch_gene_for_stable_id(GtEnsemblGeneAdaptor *ga,
                                      GtFeatureIndex *gfi,
                                      GtGenomeNode **gn,
                                      const char *stable_id,
                                      GtError *err);
int gt_ensembl_fetch_genes_for_range(GtEnsemblGeneAdaptor *ga,
                                     GtFeatureIndex *gfi,
                                     GtArray *results,
                                     const char *seqid,
                                     const GtRange *qry_range,
                                     char* biotype_filter[],
                                     int b_length,
                                     GtError *err);
void gt_ensembl_gene_adaptor_delete(GtEnsemblGeneAdaptor *ga);
                                      
int gt_ensembl_gene_adaptor_unit_test(GtError *err);

#endif
