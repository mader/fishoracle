-- fishoracle database (former tamex)

-- 
-- Table structure for table `area_access`
-- 

CREATE TABLE `area_access` (
  `area_access_area_name` varchar(30) NOT NULL,
  `area_access_user_id` int(11) NOT NULL default '0',
  `area_access_table_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`area_access_area_name`,`area_access_user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

-- 
-- Table structure for table `chip`
-- 

CREATE TABLE `chip` (
  `chip_name` varchar(25) NOT NULL,
  `chip_type` varchar(11) NOT NULL,
  `chip_cdf-file` varchar(25) NOT NULL,
  PRIMARY KEY  (`chip_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Dumping data for table `chip_table`
-- 

INSERT INTO `chip` (`chip_name`, `chip_type`, `chip_cdf-file`) VALUES 
('mapping10k_xba142', 'snp', 'Mapping10K_Xba142.CDF'),
('mapping50k_hind240', 'snp', 'Mapping50K_Hind.CDF'),
('mapping50k_xba240', 'snp', 'Mapping50K_Xba.CDF'),
('mapping250k_nsp', 'snp', 'Mapping250k_nsp.CDF'),
('mapping250k_sty', 'snp', 'Mapping250k_sty.CDF'),
('hg-u133a_2', 'expression', 'HG-U133A_2.CDF'),
('GenomeWideSNP_6', 'snp', 'GenomeWideSNP_6.Full.cdf');

-- --------------------------------------------------------

-- 
-- Table structure for table `meta`
-- 

CREATE TABLE `meta_status` (
  `meta_status_id` int(11) NOT NULL auto_increment,
  `meta_status_label` varchar(30) NOT NULL,
  `meta_status_activity` varchar(30) NOT NULL,
  PRIMARY KEY  (`meta_status_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Dumping data for table `meta_status_table`
-- 

INSERT INTO `meta_status` (`meta_status_id`, `meta_status_label`, `meta_status_activity`) VALUES 
(1, 'Primary N0', 'enabled'),
(2, 'Primary N+', 'enabled'),
(3, 'LN Metastasis', 'enabled'),
(4, 'Distant Metastasis', 'enabled'),
(5, 'unknown', 'enabled');

-- --------------------------------------------------------

-- 
-- Table structure for table `microarraystudy`
-- 

CREATE TABLE `microarraystudy` (
  `microarraystudy_id` int(11) NOT NULL auto_increment,
  `microarraystudy_date_inserted` date NOT NULL,
  `microarraystudy_labelling` varchar(255) NOT NULL,
  `microarraystudy_description` varchar(255) NOT NULL,
  `microarraystudy_link_preprocessed_data` varchar(80) NOT NULL,
  `microarraystudy_user_id` int(11) NOT NULL,
  `microarraystudy_normalized_with` varchar(30) NOT NULL,
  `microarraystudy_sample_on_chip_id` int NOT NULL,
  PRIMARY KEY  (`microarraystudy_id`),
  UNIQUE KEY `microarraystudy_labelling` (`microarraystudy_labelling`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

-- 
-- Table structure for table `organ`
-- 

CREATE TABLE `organ` (
  `organ_id` int(11) NOT NULL auto_increment,
  `organ_label` varchar(30) NOT NULL,
  `organ_activity` varchar(30) NOT NULL,
  PRIMARY KEY  (`organ_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Dumping data for table `organ`
-- 

INSERT INTO `organ` (`organ_id`, `organ_label`, `organ_activity`) VALUES 
(1, 'Prostate', 'enabled'),
(2, 'Kidney', 'enabled'),
(3, 'Esophagus', 'enabled'),
(4, 'Pancreas', 'enabled'),
(5, 'Lung', 'enabled'),
(6, 'Colon', 'enabled'),
(7, 'Breast', 'enabled'),
(8, 'Oral cavity', 'enabled'),
(9, 'Ovary', 'enabled'),
(10, 'Endometrium', 'enabled'),
(11, 'Bone', 'enabled'),
(12, 'Lymphatic tissue', 'enabled'),
(13, 'Bladder', 'enabled'),
(14, 'Cell line', 'enabled'),
(15, 'zest', 'disabled');

-- --------------------------------------------------------

-- 
-- Table structure for table `patho_grade`
-- 

CREATE TABLE `patho_grade` (
  `patho_grade_id` int(11) NOT NULL auto_increment,
  `patho_grade_label` varchar(30) NOT NULL,
  `patho_grade_activity` varchar(30) NOT NULL,
  PRIMARY KEY  (`patho_grade_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Dumping data for table `patho_grade`
-- 

INSERT INTO `patho_grade` (`patho_grade_id`, `patho_grade_label`, `patho_grade_activity`) VALUES 
(1, 'G0', 'enabled'),
(2, 'G1', 'enabled'),
(3, 'G2', 'enabled'),
(4, 'G3', 'enabled'),
(5, 'unknown', 'enabled'),
(6, 'zest', 'disabled');

-- --------------------------------------------------------

-- 
-- Table structure for table `patho_stage`
-- 

CREATE TABLE `patho_stage` (
  `patho_stage_id` int(11) NOT NULL auto_increment,
  `patho_stage_label` varchar(30) NOT NULL,
  `patho_stage_activity` varchar(30) NOT NULL,
  PRIMARY KEY  (`patho_stage_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Dumping data for table `patho_stage`
-- 

INSERT INTO `patho_stage` (`patho_stage_id`, `patho_stage_label`, `patho_stage_activity`) VALUES 
(1, 'pT1', 'enabled'),
(2, 'pT2', 'enabled'),
(3, 'pT3', 'enabled'),
(4, 'pT4', 'enabled'),
(5, 'unknown', 'enabled'),
(6, 'premalignant', 'enabled'),
(7, 'zest', 'disabled');

-- --------------------------------------------------------

-- 
-- Table structure for table `cnc_segment`
-- 

CREATE TABLE `cnc_segment` (
  `cnc_segment_id` int UNSIGNED NOT NULL auto_increment,
  `cnc_segment_stable_id` varchar(16),
  `cnc_segment_chromosome` varchar(8) NOT NULL,
  `cnc_segment_start` int NOT NULL,
  `cnc_segment_end` int NOT NULL,
  `cnc_segment_mean` double,
  `cnc_segment_markers` int,
  `cnc_segment_import_date` date,
  `cnc_segment_microarraystudy_id` int,
  PRIMARY KEY(`cnc_segment_id`)
);

-- --------------------------------------------------------

-- 
-- Table structure for table `sample_on_chip`
-- 

CREATE TABLE `sample_on_chip` (
  sample_on_chip_id int UNSIGNED NOT NULL auto_increment,
  `sample_on_chip_chip_name` varchar(25) NOT NULL,
  `sample_on_chip_tissue_sample_id` int(11) NOT NULL,
  `sample_on_chip_user_id` int(11) NOT NULL,
  `sample_on_chip_date_inserted` date NOT NULL,
  `sample_on_chip_belongs_to_sample_on_chip_id` int(11) default NULL,
  `sample_on_chip_celfile_name` varchar(30) NOT NULL,
  `sample_on_chip_preprocessed` varchar(20) NOT NULL,
  `sample_on_chip_microarray_study_id` int NOT NULL,
  PRIMARY KEY  (`sample_on_chip_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

-- 
-- Table structure for table `tissue_sample`
-- 

CREATE TABLE `tissue_sample` (
  `tissue_sample_id` int(11) NOT NULL auto_increment,
  `tissue_sample_sample_id` varchar(25) NOT NULL,
  `tissue_sample_organ_id` int(11) NOT NULL,
  `tissue_sample_patho_stage_id` int(11) NOT NULL,
  `tissue_sample_patho_grade_id` int(11) NOT NULL,
  `tissue_sample_meta_status_id` int(11) NOT NULL,
  PRIMARY KEY  (`tissue_sample_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

-- 
-- Table structure for table `user`
-- 

CREATE TABLE `user` (
  `user_id` int unsigned not null auto_increment,
  `first_name` varchar(128),
  `last_name` varchar(128),
  `username` varchar(128) not null,
  `email` varchar(128) not null,
  `password` varchar(128) not null,
  `isactive` int not null,
  `isadmin` int not null,
  PRIMARY KEY  (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;
