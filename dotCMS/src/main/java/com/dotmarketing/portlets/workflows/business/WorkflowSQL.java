package com.dotmarketing.portlets.workflows.business;

import com.dotmarketing.db.DbConnectionFactory;

abstract class WorkflowSQL {
	protected static final String MYSQL = "MySQL";
	protected static final String POSTGRESQL = "PostgreSQL";
	protected static final String ORACLE = "Oracle";
	protected static final String MSSQL = "Microsoft SQL Server";

	static protected WorkflowSQL getInstance() {
		if (DbConnectionFactory.isMySql()) {
			return new MySQLWorkflowSQL();
		} else if (DbConnectionFactory.isPostgres()) {
			return new PostgresWorkflowSQL();
		} else if (DbConnectionFactory.isMsSql()) {
			return new MSSQLWorkflowSQL();
		} else if (DbConnectionFactory.isOracle()) {
			return new OracleWorkflowSQL();
		} else if (DbConnectionFactory.isH2()) {
		    return new H2WorkflowSQL();
		}
		return null;
	}



	protected static String SELECT_SCHEMES = "select * from workflow_scheme where (archived = ? or archived = ?) order by name";

	protected static String SELECT_SCHEME= "select * from workflow_scheme where id = ?";
	protected static String SELECT_SCHEME_NAME= "select * from workflow_scheme where name = ?";



	protected static String SELECT_DEFAULT_SCHEME= "select * from workflow_scheme where default_scheme = " + DbConnectionFactory.getDBTrue() + " ";

	protected static String UPDATE_SCHEME_SET_TO_DEFAULT= "update workflow_scheme set default_scheme = " + DbConnectionFactory.getDBTrue() + " where id = ? ";


	protected static String SELECT_TASK_NULL_BY_STRUCT= "select task.* FROM workflow_task task INNER JOIN contentlet con ON con.identifier = task.webasset INNER JOIN contentlet_version_info cvi ON cvi.working_inode = con.inode"
			+ " WHERE task.status is NULL AND con.structure_inode=?";

	protected static String SELECT_NULL_TASK_CONTENTLET_FOR_WORKFLOW = "select c.identifier from contentlet c join workflow_scheme_x_structure wss on c.structure_inode = wss.structure_id and wss.scheme_id = ? where c.identifier in (select t.webasset from workflow_task t where t.status is null)";
	protected static String SELECT_TASK_STEPS_TO_CLEAN_BY_STRUCT= "select * from  workflow_task where webasset in (select identifier from contentlet where contentlet.structure_inode = ? group by identifier) ";
	protected static String UPDATE_STEPS_BY_STRUCT= "update workflow_task set status = ? where webasset in (select identifier from contentlet where contentlet.structure_inode = ? group by identifier) ";
	protected static String DELETE_SCHEME_FOR_STRUCT= "delete from workflow_scheme_x_structure where structure_id = ?";
	protected static String INSERT_SCHEME_FOR_STRUCT= "insert into workflow_scheme_x_structure (id, scheme_id, structure_id) values ( ?, ?, ?)";
	protected static String SELECT_SCHEME_BY_STRUCT= "select * from workflow_scheme, workflow_scheme_x_structure where workflow_scheme.id = workflow_scheme_x_structure.scheme_id and workflow_scheme_x_structure.structure_id = ?";

	protected static String INSERT_SCHEME= "insert into workflow_scheme (id, name, description, archived, mandatory, default_scheme, mod_date) values (?,?,?,?,?,?,?)";

	protected static String UPDATE_SCHEME= "update workflow_scheme set name = ?, description =?, archived=?, mandatory=?, mod_date=? where id =? ";

	protected static String SELECT_STEPS_BY_SCHEME= "select * from workflow_step where scheme_id = ? order by  my_order";
	protected static String SELECT_ACTIONS_BY_SCHEME= "select * from workflow_action where scheme_id = ? order by  name";
	protected static String SELECT_ACTIONS_BY_STEP =
		"select workflow_action.* from workflow_action join workflow_action_step on workflow_action.id = workflow_action_step.action_id  where workflow_action_step.step_id = ? order by  action_order";
	protected static String SELECT_ACTION= "select * from workflow_action where id = ? ";

	/**
	 * Select to get the action associated to the step.
	 */
	protected static String SELECT_ACTION_BY_STEP  = "select workflow_action.* from workflow_action join workflow_action_step on workflow_action.id = workflow_action_step.action_id  where workflow_action_step.action_id = ? and workflow_action_step.step_id = ?";

	/**
	 * Select to get the steps ids associated to the action
	 */
	protected static String SELECT_STEPS_ID_BY_ACTION  = "select workflow_action_step.step_id as stepid from workflow_action_step  where workflow_action_step.action_id = ?";

	protected static String INSERT_ACTION_FOR_STEP = "insert into workflow_action_step(action_id, step_id, action_order) values (?,?,?)";
	protected static String UPDATE_ACTION_FOR_STEP_ORDER = "update workflow_action_step set action_order=? where action_id=? and step_id=?";
	protected static String INSERT_ACTION= "insert into workflow_action (id, scheme_id, name, condition_to_progress, next_step_id, next_assign, my_order, assignable, commentable, icon, use_role_hierarchy_assign, requires_checkout, show_on) values (?, ?, ?, ?, ?, ?, ?,?, ?, ?,?,?,?)";
	//protected static String UPDATE_ACTION= "update  workflow_action set scheme_id=?, name=?,  condition_to_progress=?, next_step_id=?, next_assign=?, my_order=?, assignable=?, commentable=?, icon=?, use_role_hierarchy_assign=?,requires_checkout=?,requires_checkout_option=? where id=?";
	protected static String UPDATE_ACTION= "update  workflow_action set scheme_id=?, name=?,  condition_to_progress=?, next_step_id=?, next_assign=?, my_order=?, assignable=?, commentable=?, icon=?, use_role_hierarchy_assign=?,requires_checkout=?,show_on=? where id=?";
	protected static String DELETE_ACTION= "delete from workflow_action where id = ? ";
	protected static String DELETE_ACTION_STEP     = "delete from workflow_action_step where action_id =? and step_id =? ";
	protected static String DELETE_ACTIONS_STEP    = "delete from workflow_action_step where step_id   =? ";
	protected static String DELETE_ACTIONS_BY_STEP = "delete from workflow_action_step where action_id =? ";

	protected static String SELECT_STEP= "select * from workflow_step where id = ? ";
	protected static String INSERT_STEP= "insert into workflow_step (id, name, scheme_id,my_order,resolved,escalation_enable,escalation_action,escalation_time) values (?, ?, ?, ?, ?, ?, ?, ?) ";
	protected static String UPDATE_STEP= "update workflow_step set name=?, scheme_id=?, my_order=?, resolved = ?, escalation_enable = ?, escalation_action=?, escalation_time = ? where id = ?";
	protected static String DELETE_STEP= "delete from workflow_step where id = ?";
	protected static String SELECT_STEP_BY_CONTENTLET= "select workflow_task.id as workflowid, workflow_step.* from workflow_step join workflow_task on workflow_task.status = workflow_step.id where workflow_task.webasset= ? and workflow_task.language_id = ?";
	protected static String RESET_CONTENTLET_STEPS= "update workflow_task set status = ? where webasset= ?";
	protected static String DELETE_CONTENTLET_STEPS= "delete from workflow_task where status = ? and webasset= ?";
	protected static String SELECT_COUNT_CONTENTLES_BY_STEP= "select count(workflow_task.id) as count from workflow_task join workflow_step on workflow_task.status=workflow_step.id where workflow_step.id=?";

	protected static String SELECT_ACTION_CLASSES_BY_ACTION= "select * from workflow_action_class where action_id = ? order by  my_order";
	protected static String SELECT_ACTION_CLASS= "select * from workflow_action_class where id = ? ";
	protected static String INSERT_ACTION_CLASS= "insert into workflow_action_class (id, action_id, name, my_order, clazz) values (?,?, ?, ?, ?)";
	protected static String UPDATE_ACTION_CLASS= "update workflow_action_class set action_id= ?, name=?, my_order=?, clazz=? where id =?";
	protected static String DELETE_ACTION_CLASS= "delete from workflow_action_class where id =?";
	protected static String DELETE_ACTION_CLASS_BY_ACTION= "delete from workflow_action_class where action_id =?";

	protected static String SELECT_ACTION_CLASS_PARAMS_BY_ACTIONCLASS= "select * from workflow_action_class_pars where workflow_action_class_id = ?";
	protected static String SELECT_ACTION_CLASS_PARAM= "select * from workflow_action_class_pars where id = ? ";
	protected static String INSERT_ACTION_CLASS_PARAM= "insert into workflow_action_class_pars (id,workflow_action_class_id,key,value) values (?,?, ?, ?)";
	protected static String UPDATE_ACTION_CLASS_PARAM= "update workflow_action_class_pars set workflow_action_class_id= ?, key=?, value=? where id =?";
	protected static String DELETE_ACTION_CLASS_PARAM_BY_ACTION_CLASS= "delete from workflow_action_class_pars where workflow_action_class_id =?";
	protected static String DELETE_ACTION_CLASS_PARAM_BY_ID="delete from workflow_action_class_pars where id=?";

	// chri
    protected static String UPDATE_USER_ASSIGNTO_TASK = "update workflow_task set assigned_to = ? where id = ?";
    protected static String RETRIEVE_LAST_STEP_ACTIONID = "select  * from workflow_history where workflowtask_id = ? order by creation_date desc";
    protected static String RETRIEVE_TASK = "select  * from workflow_history where workflowtask_id = ? order by creation_date desc";
    // chri

    protected static String SELECT_EXPIRED_TASKS = "";

	protected static String SELECT_TASK = "SELECT * FROM workflow_task WHERE webasset = ? AND language_id = ?";
    protected static String SELECT_TASKS_BY_STEP="select * from workflow_task where status = ?";
	protected static String SELECT_STRUCTS_FOR_SCHEME="select st.* from structure st join workflow_scheme_x_structure wss on st.inode = wss.structure_id where wss.scheme_id = ? order by st.name";
    protected static String DELETE_STRUCTS_FOR_SCHEME="delete from workflow_scheme_x_structure where scheme_id = ?";
	protected static String DELETE_SCHEME="delete from workflow_scheme where id = ?";
}

