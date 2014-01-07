INSERT INTO `generator` VALUES ('app_attr_gen',1000),('app_com_gen',1000),('app_gen',1000),('bridge_gen',1000),('group_gen',1000),('imp_attr_gen',1000),('imp_com_gen',1000),('imp_file_gen',1000),('imp_gen',1000),('plat_gen',1000),('user_gen',1000);

INSERT INTO `repo_user` VALUES (1,'admin','b91cd1a54781790beaa2baf741fa6789','SHIWA Repo Administrator','SHIWA','admin@dev.null',1,1,1,1);

INSERT INTO `role_flags` VALUES (1,0,'admin'),(0,1,'validator'),(0,0,'user');

INSERT INTO operating_systems VALUES (null, "SciLinux", "5.0.1");
INSERT INTO operating_systems VALUES (null, "SciLinux", "6.0.1");
INSERT INTO operating_systems VALUES (null, "Windows 8", "9010");
INSERT INTO operating_systems VALUES (null, "Debian", "6.1");

INSERT INTO job_type VALUES (null, "single");
INSERT INTO job_type VALUES (null, "JavaJob");
INSERT INTO job_type VALUES (null, "MPI");

INSERT INTO job_manager VALUES (null, "PBS");
INSERT INTO job_manager VALUES (null, "Condor");
INSERT INTO job_manager VALUES (null, "LSF");

/*inserting test values in the table Backend*/
INSERT INTO backend VALUES (null, "GT4", "A new Backend!");
INSERT INTO backend VALUES (null, "GT2", "A second Backend!");
INSERT INTO backend VALUES (null, "gLite", "An old backend!");

INSERT INTO supported_job_managers VALUES (1, 1);
INSERT INTO supported_job_managers VALUES (1, 2);
INSERT INTO supported_job_managers VALUES (2, 1);
INSERT INTO supported_job_managers VALUES (3, 2);
INSERT INTO supported_job_managers VALUES (3, 3);
INSERT INTO supported_job_managers VALUES (1, 3);
