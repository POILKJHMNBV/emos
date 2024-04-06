-- ----------------------------
-- Table structure for QRTZ_JOB_DETAILS
-- ----------------------------
create table if not exists QRTZ_JOB_DETAILS
(
    SCHED_NAME        varchar(120) not null,
    JOB_NAME          varchar(190) not null,
    JOB_GROUP         varchar(190) not null,
    DESCRIPTION       varchar(250) null,
    JOB_CLASS_NAME    varchar(250) not null,
    IS_DURABLE        varchar(1)   not null,
    IS_NONCONCURRENT  varchar(1)   not null,
    IS_UPDATE_DATA    varchar(1)   not null,
    REQUESTS_RECOVERY varchar(1)   not null,
    JOB_DATA          blob         null,
    primary key (SCHED_NAME, JOB_NAME, JOB_GROUP)
);
create index IDX_QRTZ_J_GRP on QRTZ_JOB_DETAILS (SCHED_NAME, JOB_GROUP);
create index IDX_QRTZ_J_REQ_RECOVERY on QRTZ_JOB_DETAILS (SCHED_NAME, REQUESTS_RECOVERY);

-- ----------------------------
-- Table structure for QRTZ_TRIGGERS
-- ----------------------------
create table if not exists QRTZ_TRIGGERS
(
    SCHED_NAME     varchar(120) not null,
    TRIGGER_NAME   varchar(190) not null,
    TRIGGER_GROUP  varchar(190) not null,
    JOB_NAME       varchar(190) not null,
    JOB_GROUP      varchar(190) not null,
    DESCRIPTION    varchar(250) null,
    NEXT_FIRE_TIME bigint       null,
    PREV_FIRE_TIME bigint       null,
    PRIORITY       int          null,
    TRIGGER_STATE  varchar(16)  not null,
    TRIGGER_TYPE   varchar(8)   not null,
    START_TIME     bigint       not null,
    END_TIME       bigint       null,
    CALENDAR_NAME  varchar(190) null,
    MISFIRE_INSTR  smallint     null,
    JOB_DATA       blob         null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_TRIGGERS_ibfk_1 foreign key (SCHED_NAME, JOB_NAME, JOB_GROUP) references QRTZ_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP)
);
create index IDX_QRTZ_T_C on QRTZ_TRIGGERS (SCHED_NAME, CALENDAR_NAME);
create index IDX_QRTZ_T_G on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);
create index IDX_QRTZ_T_J on QRTZ_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);
create index IDX_QRTZ_T_JG on QRTZ_TRIGGERS (SCHED_NAME, JOB_GROUP);
create index IDX_QRTZ_T_NEXT_FIRE_TIME on QRTZ_TRIGGERS (SCHED_NAME, NEXT_FIRE_TIME);
create index IDX_QRTZ_T_NFT_MISFIRE on QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME);
create index IDX_QRTZ_T_NFT_ST on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_STATE, NEXT_FIRE_TIME);
create index IDX_QRTZ_T_NFT_ST_MISFIRE on QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_STATE);
create index IDX_QRTZ_T_NFT_ST_MISFIRE_GRP on QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_GROUP, TRIGGER_STATE);
create index IDX_QRTZ_T_N_G_STATE on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_GROUP, TRIGGER_STATE);
create index IDX_QRTZ_T_N_STATE on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP, TRIGGER_STATE);
create index IDX_QRTZ_T_STATE on QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_STATE);

-- ----------------------------
-- Table structure for QRTZ_LOCKS
-- ----------------------------
create table if not exists QRTZ_LOCKS
(
    SCHED_NAME varchar(120) not null,
    LOCK_NAME  varchar(40)  not null,
    primary key (SCHED_NAME, LOCK_NAME)
);

-- ----------------------------
-- Table structure for QRTZ_PAUSED_TRIGGER_GRPS
-- ----------------------------
create table if not exists QRTZ_PAUSED_TRIGGER_GRPS
(
    SCHED_NAME    varchar(120) not null,
    TRIGGER_GROUP varchar(190) not null,
    primary key (SCHED_NAME, TRIGGER_GROUP)
);

-- ----------------------------
-- Table structure for QRTZ_SCHEDULER_STATE
-- ----------------------------
create table if not exists QRTZ_SCHEDULER_STATE
(
    SCHED_NAME        varchar(120) not null,
    INSTANCE_NAME     varchar(190) not null,
    LAST_CHECKIN_TIME bigint       not null,
    CHECKIN_INTERVAL  bigint       not null,
    primary key (SCHED_NAME, INSTANCE_NAME)
);

-- ----------------------------
-- Table structure for QRTZ_SIMPLE_TRIGGERS
-- ----------------------------
create table if not exists QRTZ_SIMPLE_TRIGGERS
(
    SCHED_NAME      varchar(120) not null,
    TRIGGER_NAME    varchar(190) not null,
    TRIGGER_GROUP   varchar(190) not null,
    REPEAT_COUNT    bigint       not null,
    REPEAT_INTERVAL bigint       not null,
    TIMES_TRIGGERED bigint       not null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_SIMPLE_TRIGGERS_ibfk_1 foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

-- ----------------------------
-- Table structure for QRTZ_SIMPROP_TRIGGERS
-- ----------------------------
create table if not exists QRTZ_SIMPROP_TRIGGERS
(
    SCHED_NAME    varchar(120)   not null,
    TRIGGER_NAME  varchar(190)   not null,
    TRIGGER_GROUP varchar(190)   not null,
    STR_PROP_1    varchar(512)   null,
    STR_PROP_2    varchar(512)   null,
    STR_PROP_3    varchar(512)   null,
    INT_PROP_1    int            null,
    INT_PROP_2    int            null,
    LONG_PROP_1   bigint         null,
    LONG_PROP_2   bigint         null,
    DEC_PROP_1    decimal(13, 4) null,
    DEC_PROP_2    decimal(13, 4) null,
    BOOL_PROP_1   varchar(1)     null,
    BOOL_PROP_2   varchar(1)     null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_SIMPROP_TRIGGERS_ibfk_1 foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

-- ----------------------------
-- Table structure for QRTZ_BLOB_TRIGGERS
-- ----------------------------
create table QRTZ_BLOB_TRIGGERS
(
    SCHED_NAME    varchar(120) not null,
    TRIGGER_NAME  varchar(190) not null,
    TRIGGER_GROUP varchar(190) not null,
    BLOB_DATA     blob         null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_BLOB_TRIGGERS_ibfk_1 foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);
create index SCHED_NAME on QRTZ_BLOB_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

-- ----------------------------
-- Table structure for QRTZ_CALENDARS
-- ----------------------------
create table if not exists QRTZ_CALENDARS
(
    SCHED_NAME    varchar(120) not null,
    CALENDAR_NAME varchar(190) not null,
    CALENDAR      blob         not null,
    primary key (SCHED_NAME, CALENDAR_NAME)
);

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
create table if not exists QRTZ_CRON_TRIGGERS
(
    SCHED_NAME      varchar(120) not null,
    TRIGGER_NAME    varchar(190) not null,
    TRIGGER_GROUP   varchar(190) not null,
    CRON_EXPRESSION varchar(120) not null,
    TIME_ZONE_ID    varchar(80)  null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_CRON_TRIGGERS_ibfk_1 foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

-- ----------------------------
-- Table structure for QRTZ_FIRED_TRIGGERS
-- ----------------------------
create table if not exists QRTZ_FIRED_TRIGGERS
(
    SCHED_NAME        varchar(120) not null,
    ENTRY_ID          varchar(95)  not null,
    TRIGGER_NAME      varchar(190) not null,
    TRIGGER_GROUP     varchar(190) not null,
    INSTANCE_NAME     varchar(190) not null,
    FIRED_TIME        bigint       not null,
    SCHED_TIME        bigint       not null,
    PRIORITY          int          not null,
    STATE             varchar(16)  not null,
    JOB_NAME          varchar(190) null,
    JOB_GROUP         varchar(190) null,
    IS_NONCONCURRENT  varchar(1)   null,
    REQUESTS_RECOVERY varchar(1)   null,
    primary key (SCHED_NAME, ENTRY_ID)
);
create index IDX_QRTZ_FT_INST_JOB_REQ_RCVRY on QRTZ_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME, REQUESTS_RECOVERY);
create index IDX_QRTZ_FT_JG on QRTZ_FIRED_TRIGGERS (SCHED_NAME, JOB_GROUP);
create index IDX_QRTZ_FT_J_G on QRTZ_FIRED_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);
create index IDX_QRTZ_FT_TG on QRTZ_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);
create index IDX_QRTZ_FT_TRIG_INST_NAME on QRTZ_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME);
create index IDX_QRTZ_FT_T_G on QRTZ_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

-- ----------------------------
-- Table structure for tb_action
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tb_action`  (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `action_code` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '行为编号',
  `action_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '行为名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unq_action_name`(`action_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '行为表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_action
-- ----------------------------
INSERT INTO `tb_action` VALUES (1, 'INSERT', '添加');
INSERT INTO `tb_action` VALUES (2, 'DELETE', '删除');
INSERT INTO `tb_action` VALUES (3, 'UPDATE', '修改');
INSERT INTO `tb_action` VALUES (4, 'SELECT', '查询');
INSERT INTO `tb_action` VALUES (5, 'APPROVAL', '审批');
INSERT INTO `tb_action` VALUES (6, 'EXPORT', '导出');
INSERT INTO `tb_action` VALUES (7, 'BACKUP', '备份');
INSERT INTO `tb_action` VALUES (8, 'ARCHIVE', '归档');

-- ----------------------------
-- Table structure for tb_amect
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tb_amect`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uuid` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'UUID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `amount` decimal(10, 2) UNSIGNED NOT NULL COMMENT '罚款金额',
  `type_id` int(11) NOT NULL COMMENT '罚款类型',
  `reason` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '罚款原因',
  `prepay_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微信预支付交易会话标识',
  `transaction_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微信订单号',
  `status` tinyint(4) NOT NULL COMMENT '状态：1未缴纳，2已缴纳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unq_uuid`(`uuid`) USING BTREE,
  UNIQUE INDEX `idx_prepayid`(`prepay_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_type_id`(`type_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '罚金表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_amect_type
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tb_amect_type`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类别',
  `money` decimal(10, 2) UNSIGNED NOT NULL COMMENT '罚金',
  `systemic` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为系统内置',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unq_type`(`type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '罚金类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_amect_type
-- ----------------------------
INSERT INTO `tb_amect_type` VALUES (1, '迟到早退', 20.00, 1);
INSERT INTO `tb_amect_type` VALUES (2, '旷工', 500.00, 1);
INSERT INTO `tb_amect_type` VALUES (3, '打架', 200.00, 1);
INSERT INTO `tb_amect_type` VALUES (4, '辱骂同事', 100.00, 1);
INSERT INTO `tb_amect_type` VALUES (5, '偷窃物品', 2000.00, 1);
INSERT INTO `tb_amect_type` VALUES (6, '未关电脑', 50.00, 1);
INSERT INTO `tb_amect_type` VALUES (7, '未穿工装', 200.00, 1);
INSERT INTO `tb_amect_type` VALUES (8, '未佩戴工牌', 50.00, 1);
INSERT INTO `tb_amect_type` VALUES (9, '缺席会议', 100.00, 1);

CREATE TABLE IF NOT EXISTS `tb_dept`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dept_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '部门名称',
  `tel` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门电话',
  `email` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门邮箱',
  `desc` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unq_dept_name`(`dept_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_dept
-- ----------------------------
INSERT INTO `tb_dept` VALUES (1, '管理部', '010-12345678', 'manage@163.com', '管理部负责管理公司所有事务');
INSERT INTO `tb_dept` VALUES (2, '行政部', NULL, 'tech@163.com', NULL);
INSERT INTO `tb_dept` VALUES (3, '技术部', '010-12345678', 'tech@163.com', NULL);
INSERT INTO `tb_dept` VALUES (4, '市场部', NULL, 'tech@163.com', NULL);
INSERT INTO `tb_dept` VALUES (5, '后勤部', NULL, 'tech@163.com', NULL);
INSERT INTO `tb_dept` VALUES (6, '人事部', NULL, '106135489@qq.com', NULL);

-- ----------------------------
-- Table structure for tb_leave
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tb_leave`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int(10) UNSIGNED NOT NULL COMMENT '用户ID',
  `reason` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '请假原因',
  `start` datetime NOT NULL COMMENT '开始时间',
  `end` datetime NOT NULL COMMENT '结束时间',
  `days` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '天数',
  `type` tinyint(3) UNSIGNED NOT NULL COMMENT '类型：1病假，2事假',
  `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1请假中，2不同意，3已同意',
  `instance_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工作流实例ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT '请假信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_meeting
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tb_meeting`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uuid` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'UUID',
  `title` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '会议题目',
  `creator_id` bigint(20) NOT NULL COMMENT '创建人ID',
  `date` date NOT NULL COMMENT '日期',
  `place` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '开会地点',
  `start` time NOT NULL COMMENT '开始时间',
  `end` time NOT NULL COMMENT '结束时间',
  `type` smallint(6) NOT NULL COMMENT '会议类型（1在线会议，2线下会议）',
  `members` json NOT NULL COMMENT '参与者',
  `desc` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '会议内容',
  `instance_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工作流实例ID',
  `present` json NULL COMMENT '出席人员名单',
  `unpresent` json NULL COMMENT '未出席人员名单',
  `status` smallint(6) NOT NULL COMMENT '状态（1.申请中，2.审批未通过，3.审批通过，4.会议进行中，5.会议结束，6.未审批）',
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_creator_id`(`creator_id`) USING BTREE,
  INDEX `idx_date`(`date`) USING BTREE,
  INDEX `idx_type`(`type`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '会议表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_meeting_room
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tb_meeting_room`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '会议室名称',
  `max` smallint(6) NOT NULL COMMENT '最大人数',
  `desc` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态，0不可用，1可用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '会议室表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_meeting_room
-- ----------------------------
INSERT INTO `tb_meeting_room` VALUES (1, '大会议室A01', 80, '公司公共会议室', 1);
INSERT INTO `tb_meeting_room` VALUES (4, '大会议室A02', 30, '销售部会议室', 1);
INSERT INTO `tb_meeting_room` VALUES (5, '小会议室C01', 10, '公共会议室', 1);

-- ----------------------------
-- Table structure for tb_module
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tb_module`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `module_code` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模块编号',
  `module_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模块名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unq_module_id`(`module_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '模块资源表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_module
-- ----------------------------
INSERT INTO `tb_module` VALUES (1, 'USER', '用户管理');
INSERT INTO `tb_module` VALUES (2, 'EMPLOYEE', '员工管理');
INSERT INTO `tb_module` VALUES (3, 'DEPT', '部门管理');
INSERT INTO `tb_module` VALUES (4, 'MEETING', '会议管理');
INSERT INTO `tb_module` VALUES (5, 'WORKFLOW', '工作流管理');
INSERT INTO `tb_module` VALUES (6, 'MEETING_ROOM', '会议室管理');
INSERT INTO `tb_module` VALUES (7, 'ROLE', '角色管理');
INSERT INTO `tb_module` VALUES (8, 'LEAVE', '请假管理');
INSERT INTO `tb_module` VALUES (9, 'FILE', '文件管理');
INSERT INTO `tb_module` VALUES (10, 'AMECT', '罚款管理');
INSERT INTO `tb_module` VALUES (11, 'REIM', '报销管理');


-- ----------------------------
-- Table structure for tb_permission
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tb_permission`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `permission_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限',
  `module_id` int(10) UNSIGNED NOT NULL COMMENT '模块ID',
  `action_id` int(10) UNSIGNED NOT NULL COMMENT '行为ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unq_permission`(`permission_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_permission
-- ----------------------------
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('ROOT', 0, 0);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('USER:INSERT', 1, 1);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('USER:DELETE', 1, 2);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('USER:UPDATE', 1, 3);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('USER:SELECT', 1, 4);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('DEPT:INSERT', 3, 1);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('DEPT:DELETE', 3, 2);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('DEPT:UPDATE', 3, 3);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('DEPT:SELECT', 3, 4);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('MEETING:INSERT', 4, 1);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('MEETING:DELETE', 4, 2);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('MEETING:SELECT', 4, 4);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('WORKFLOW:APPROVAL', 5, 5);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('MEETING_ROOM:INSERT', 6, 1);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('MEETING_ROOM:DELETE', 6, 2);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('MEETING_ROOM:UPDATE', 6, 3);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('MEETING_ROOM:SELECT', 6, 4);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('ROLE:INSERT', 7, 1);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('ROLE:DELETE', 7, 2);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('ROLE:UPDATE', 7, 3);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('ROLE:SELECT', 7, 4);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('LEAVE:SELECT', 8, 4);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('LEAVE:INSERT', 8, 1);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('LEAVE:DELETE', 8, 2);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('FILE:ARCHIVE', 9, 8);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('AMECT:INSERT', 10, 1);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('AMECT:DELETE', 10, 2);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('AMECT:UPDATE', 10, 3);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('AMECT:SELECT', 10, 4);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('REIM:INSERT', 11, 1);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('REIM:DELETE', 11, 2);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('REIM:UPDATE', 11, 3);
INSERT INTO `tb_permission`(permission_name,module_id,action_id) VALUES ('REIM:SELECT', 11, 4);

-- ----------------------------
-- Table structure for tb_reim
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tb_reim`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `content` json NOT NULL COMMENT '报销内容',
  `amount` decimal(10, 2) NOT NULL COMMENT '总金额',
  `anleihen` decimal(10, 2) NOT NULL COMMENT '借款',
  `balance` decimal(10, 2) NOT NULL COMMENT '差额',
  `type_id` tinyint(4) NOT NULL COMMENT '类型：1普通报销，2差旅报销',
  `status` tinyint(4) NOT NULL COMMENT '状态：1审批中，2已拒绝，3审批通过，4.已归档',
  `instance_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工作流实例ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_type_id`(`type_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '报销单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_role
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tb_role`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称',
  `permissions` json NOT NULL COMMENT '权限集合',
  `desc` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `default_permissions` json NULL COMMENT '系统角色内置权限',
  `systemic` tinyint(1) NULL DEFAULT 0 COMMENT '是否为系统内置角色',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unq_role_name`(`role_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_role
-- ----------------------------
INSERT INTO tb_role (role_name, permissions, `desc`, default_permissions, systemic) VALUES ('超级管理员', '[1]', '超级管理员用户不能删除和修改', '[1]', 1);
INSERT INTO tb_role (role_name, permissions, `desc`, default_permissions, systemic) VALUES ('总经理', '[2, 3, 4, 5, 10, 11, 12, 13, 22, 23, 24, 26, 27, 28, 29, 30, 31, 32, 33]', null, '[2, 3, 4, 5, 10, 11, 12, 13, 22, 23, 24, 26, 27, 28, 29, 30, 31, 32, 33]', 1);
INSERT INTO tb_role (role_name, permissions, `desc`, default_permissions, systemic) VALUES ('部门经理', '[2, 3, 4, 5, 10, 11, 12, 13, 22, 23, 24, 26, 27, 28, 29, 30, 31, 32, 33]', null, '[2, 3, 4, 5, 10, 11, 12, 13, 22, 23, 24, 26, 27, 28, 29, 30, 31, 32, 33]', 1);
INSERT INTO tb_role (role_name, permissions, `desc`, default_permissions, systemic) VALUES ('普通员工', '[10, 11, 12, 22, 23, 24, 29, 30, 31, 32, 33]', null, '[10, 11, 12, 22, 23, 24, 29, 30, 31, 32, 33]', 1);
INSERT INTO tb_role (role_name, permissions, `desc`, default_permissions, systemic) VALUES ('HR', '[2, 3, 4, 5, 10, 11, 12, 22, 23, 24, 25, 29, 30, 31, 32, 33]', null, '[2, 3, 4, 5, 10, 11, 12, 22, 23, 24, 25, 29, 30, 31, 32, 33]', 1);
INSERT INTO tb_role (role_name, permissions, `desc`, default_permissions, systemic) VALUES ('财务', '[10, 11, 12, 22, 23, 24, 25, 29, 30, 31, 32, 33]', null, '[10, 11, 12, 22, 23, 24, 25, 29, 30, 31, 32, 33]', 1);
INSERT INTO tb_role (role_name, permissions, `desc`, default_permissions, systemic) VALUES ('HRBP', '[2, 3, 4, 5, 10, 11, 12, 22, 23, 24, 25, 29, 30, 31, 32, 33]', null, null, 0);

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tb_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `open_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '长期授权字符串',
  `nickname` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `photo` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像网址',
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `sex` enum('男','女') CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `tel` char(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `email` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `hiredate` date NULL DEFAULT NULL COMMENT '入职日期',
  `role` json NOT NULL COMMENT '角色',
  `root` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是超级管理员',
  `dept_id` int(10) UNSIGNED NULL DEFAULT NULL COMMENT '部门编号',
  `status` tinyint(4) NOT NULL COMMENT '状态 1-在职 2-离职',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unq_open_id`(`open_id`) USING BTREE,
  UNIQUE INDEX `unq_username`(`username`) USING BTREE,
  INDEX `unq_email`(`email`) USING BTREE,
  INDEX `idx_dept_id`(`dept_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB  CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO tb_user (id, username, password, open_id, nickname, photo, name, sex, tel, email, hiredate, role, root, dept_id, status, create_time) VALUES (1, 'admin', '0120E696A22BC6EC59F5203EFDB03960', 'ouEUa5aLwHh7vqrUV_xtrDeLoPLU', '拉菲…哎！', 'https://thirdwx.qlogo.cn/mmopen/vi_32/Wfic6iapia6Z12M1BWyqt3nKjEscnVfmxdDV81G16psMBec72GMCgv633ZQnkpzT9pc7LOrGtXFzVKbia5Y56YyYdg/132', '拉菲', '男', '13312345678', 'lafei@163.com', '2020-09-08', '[1]', 1, 1, 1, '2022-03-29 22:04:45');

-- ----------------------------
-- Table structure for tb_user_operate_log
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_user_operate_log
(
    id                    bigint                             not null comment '主键' primary key,
    user_id               int                                null comment '操作用户id',
    operation_description varchar(64)                        not null comment '操作描述',
    request_uri           varchar(256)                       not null comment '请求uri',
    request_method        varchar(16)                        not null comment '请求类型',
    request_ip            varchar(64)                        null comment '请求ip',
    request_parameter     json                               null comment '请求参数',
    response_status       int      default 200               not null comment '响应状态',
    cost_time             int                                not null comment '耗时 单位：ms',
    error_reason          text                               null comment '错误原因',
    create_time           datetime default CURRENT_TIMESTAMP not null comment '创建时间'
) comment '用户操作日志表';