/**
 * Copyright 1996-2013 Founder International Co.,Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author kenshin
 */
package com.founder.fix.fixflow.core.impl.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.UserTask;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.founder.fix.bpmn2extensions.coreconfig.TaskCommandDef;
import com.founder.fix.fixflow.core.cache.CacheHandler;
import com.founder.fix.fixflow.core.exception.FixFlowException;
import com.founder.fix.fixflow.core.factory.ProcessObjectFactory;
import com.founder.fix.fixflow.core.impl.Context;
import com.founder.fix.fixflow.core.impl.bpmn.behavior.ProcessDefinitionBehavior;
import com.founder.fix.fixflow.core.impl.bpmn.behavior.TaskCommandInst;
import com.founder.fix.fixflow.core.impl.db.AbstractPersistentObject;
import com.founder.fix.fixflow.core.impl.identity.GroupTo;
import com.founder.fix.fixflow.core.impl.interceptor.CommandExecutor;
import com.founder.fix.fixflow.core.impl.runtime.TokenEntity;
import com.founder.fix.fixflow.core.impl.util.ClockUtil;
import com.founder.fix.fixflow.core.impl.util.GuidUtil;
import com.founder.fix.fixflow.core.impl.util.StringUtil;
import com.founder.fix.fixflow.core.internationalization.FixFlowResources;
import com.founder.fix.fixflow.core.objkey.TaskInstanceObjKey;
import com.founder.fix.fixflow.core.runtime.ExecutionContext;
import com.founder.fix.fixflow.core.scriptlanguage.AbstractScriptLanguageMgmt;
import com.founder.fix.fixflow.core.task.Assignable;
import com.founder.fix.fixflow.core.task.DelegationState;
import com.founder.fix.fixflow.core.task.IdentityLink;
import com.founder.fix.fixflow.core.task.IdentityLinkType;
import com.founder.fix.fixflow.core.task.IncludeExclusion;
import com.founder.fix.fixflow.core.task.TaskInstanceType;
import com.founder.fix.fixflow.core.task.TaskQuery;
import com.founder.fix.fixflow.core.task.TaskDefinition;
import com.founder.fix.fixflow.core.task.TaskInstance;
import com.founder.fix.fixflow.core.task.TaskMgmtInstance;

public class TaskInstanceEntity extends AbstractPersistentObject implements TaskInstance, Assignable, Cloneable {

	private static final long serialVersionUID = 2262140765605817383L;
	
	public static final String GET_TASKINSTANCE_PERSISTENT_STATE="getTaskInstancePersistentState";
	
	public static final String GET_TASKINSTANCE_PERSISTENT_DBMAP="getTaskInstancePersistentDbMap";

	// 需要持久化的字段 //////////////////////////////////////////////////////////

	protected String id;

	protected String name;

	protected String description;

	protected String processInstanceId;

	protected String processDefinitionId;

	protected String processDefinitionKey;

	protected String processDefinitionName;

	protected int version;

	protected String tokenId;

	protected String nodeId;

	protected String nodeName;

	protected String parentTaskInstanceId;

	protected String assignee;

	protected Date claimTime;

	protected Date createTime;

	protected Date startTime;

	protected Date endTime;

	protected Date dueDate;

	protected boolean isBlocking = false;

	protected int priority = TaskInstance.PRIORITY_NORMAL;

	protected String category;

	protected String owner;

	protected DelegationState delegationState;

	protected String bizKey;

	protected String taskComment;

	protected String formUri;

	protected String formUriView;

	protected String taskGroup;

	protected TaskInstanceType taskInstanceType = TaskInstanceType.FIXFLOWTASK;

	protected boolean isCancelled = false;

	protected boolean isSuspended = false;

	protected boolean isOpen = true;

	protected boolean isDraft = false;

	protected int expectedExecutionTime = 0;

	protected String agent;

	protected String admin;

	protected String callActivityInstanceId;

	protected String pendingTaskId;

	protected Date archiveTime;

	protected String commandId;

	protected String commandType;

	protected String commandMessage;

	// get set //////////////////////////////////////////////////////////

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProcessInstanceId() {
		return this.processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getProcessDefinitionId() {
		return this.processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}

	public String getProcessDefinitionKey() {
		return this.processDefinitionKey;
	}

	public String getProcessDefinitionName() {
		return processDefinitionName;
	}

	public void setProcessDefinitionName(String processDefinitionName) {
		this.processDefinitionName = processDefinitionName;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getTokenId() {
		return this.tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getNodeId() {
		return this.nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getParentTaskInstanceId() {
		return this.parentTaskInstanceId;
	}

	public void setParentTaskInstanceId(String parentTaskInstanceId) {
		this.parentTaskInstanceId = parentTaskInstanceId;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public Date getClaimTime() {
		return this.claimTime;
	}

	public void setClaimTime(Date claimTime) {
		this.claimTime = claimTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	// 有问题的
	public DelegationState getDelegationState() {
		return delegationState;
	}

	public void setDelegationState(DelegationState delegationState) {
		this.delegationState = delegationState;
	}

	public void setDelegationStateString(String delegationState) {
		if (StringUtil.isNotEmpty(delegationState)) {
			this.delegationState = DelegationState.valueOf(delegationState);
		}
	}

	public String getBizKey() {
		return this.bizKey;
	}

	public void setBizKey(String bizKey) {
		this.bizKey = bizKey;
	}

	public String getTaskComment() {
		return taskComment;
	}

	public void setTaskComment(String taskComment) {
		this.taskComment = taskComment;
	}

	public void setFormUri(String formUri) {
		this.formUri = formUri;
	}

	public String getFormUri() {
		return this.formUri;
	}

	public String getFormUriView() {
		return formUriView;
	}

	public void setFormUriView(String formUriView) {
		this.formUriView = formUriView;
	}

	public String getTaskGroup() {
		return taskGroup;
	}

	public void setTaskGroup(String taskGroup) {
		this.taskGroup = taskGroup;
	}

	public TaskInstanceType getTaskInstanceType() {
		return taskInstanceType;
	}

	public void setTaskInstanceType(TaskInstanceType taskInstanceType) {
		this.taskInstanceType = taskInstanceType;
	}

	public void setTaskInstanceTypeString(String taskInstanceType) {
		if (StringUtil.isNotEmpty(taskInstanceType)) {
			this.taskInstanceType = TaskInstanceType.valueOf(taskInstanceType);
		}
	}

	// 有问题的
	public boolean isBlocking() {
		return isBlocking;
	}

	public void setBlockingString(String isBlocking) {
		if (StringUtil.isNotEmpty(isBlocking)) {
			this.isBlocking = StringUtil.getBoolean(isBlocking);
		}

	}

	public void setBlocking(boolean isBlocking) {
		this.isBlocking = isBlocking;
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	// 有问题的
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	// 有问题的
	public void setCancelledString(String isCancelled) {
		if (StringUtil.isNotEmpty(isCancelled)) {
			this.isCancelled = StringUtil.getBoolean(isCancelled);
		}
	}

	public boolean isSuspended() {
		return isSuspended;
	}

	// 有问题的
	public void setSuspended(boolean isSuspended) {
		this.isSuspended = isSuspended;
	}

	public void setSuspendedString(String isSuspended) {
		if (StringUtil.isNotEmpty(isSuspended)) {
			this.isSuspended = StringUtil.getBoolean(isSuspended);
		}
	}

	public boolean isOpen() {
		return isOpen;
	}

	// 有问题的
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	// 有问题的
	public void setOpenString(String isOpen) {
		if (StringUtil.isNotEmpty(isOpen)) {
			this.isOpen = StringUtil.getBoolean(isOpen);
		}
	}

	public boolean isDraft() {
		return isDraft;
	}

	public void setDraft(boolean isDraft) {
		this.isDraft = isDraft;
	}

	public void setDraftString(String isDraft) {
		if (StringUtil.isNotEmpty(isDraft)) {
			this.isDraft = StringUtil.getBoolean(isDraft);
		}
	}

	public int getExpectedExecutionTime() {
		return expectedExecutionTime;
	}

	public void setExpectedExecutionTime(int expectedExecutionTime) {
		this.expectedExecutionTime = expectedExecutionTime;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	public String getCallActivityInstanceId() {
		return callActivityInstanceId;
	}

	public void setCallActivityInstanceId(String callActivityInstanceId) {
		this.callActivityInstanceId = callActivityInstanceId;
	}

	public String getPendingTaskId() {
		return pendingTaskId;
	}

	public void setPendingTaskId(String pendingTaskId) {
		this.pendingTaskId = pendingTaskId;
	}

	public Date getArchiveTime() {
		return archiveTime;
	}

	public void setArchiveTime(Date archiveTime) {
		this.archiveTime = archiveTime;
	}

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public String getCommandType() {
		return commandType;
	}

	public void setCommandType(String commandType) {
		this.commandType = commandType;
	}

	// 有问题的
	public String getCommandMessage() {

		if (this.getCommandType() == null) {

			return commandMessage;

		}
		Boolean booleanTemp = StringUtil.getBoolean(Context.getProcessEngineConfiguration().getInternationalizationConfig().getIsEnable());

		if (booleanTemp) {
			String processId = this.getProcessDefinitionId();
			String cType = Context.getProcessEngineConfiguration().getTaskCommandDefMap().get(this.getCommandType()).getType();
			String nameTemp = null;
			if (cType.equals("system")) {
				nameTemp = Context.getProcessEngineConfiguration().getFixFlowResources()
						.getResourceName(FixFlowResources.TaskComandResource, "System_" + commandId);

			} else {
				nameTemp = Context.getProcessEngineConfiguration().getFixFlowResources().getResourceName(processId, this.nodeId + "_" + commandId);

			}

			if (nameTemp == null) {
				return commandMessage;
			}
			return nameTemp;

		} else {
			return commandMessage;
		}

	}

	public String getDefaultCommandMessage() {
		return commandMessage;
	}

	public void setCommandMessage(String commandMessage) {
		this.commandMessage = commandMessage;
	}

	// 对象化元素 //////////////////////////////////////////////////////////

	protected TokenEntity token;

	protected UserTask node;

	protected TaskDefinition taskDefinition;

	protected List<IdentityLinkEntity> taskIdentityLinks = new ArrayList<IdentityLinkEntity>();

	protected TaskInstance parentTaskInstance;

	protected TaskMgmtInstance taskMgmtInstance;

	protected Map<String, Object> extensionFields = new HashMap<String, Object>();

	/**
	 * 持久化扩展字段
	 */
	protected Map<String, Object> persistenceExtensionFields = new HashMap<String, Object>();

	public Map<String, Object> getPersistenceExtensionFields() {
		return persistenceExtensionFields;
	}

	public void setPersistenceExtensionField(String fieldName, Object value) {
		extensionFields.put(fieldName, value);
		persistenceExtensionFields.put(fieldName, value);
	}

	/**
	 * 创建任务
	 */
	public TaskInstanceEntity() {
	}

	/**
	 * 创建任务
	 * 
	 * @param taskInstanceId
	 *            任务编号
	 */
	public TaskInstanceEntity(String taskInstanceId) {
		this.id = taskInstanceId;
	}

	public static TaskInstanceEntity create() {
		TaskInstanceEntity task = new TaskInstanceEntity();
		task.createTime = ClockUtil.getCurrentTime();
		return task;
	}

	public TaskInstance getParentTaskInstance() {
		return parentTaskInstance;
	}

	public TaskDefinition getTaskDefinition() {
		return taskDefinition;
	}

	public TokenEntity getToken() {
		return token;
	}

	public void setParentTaskInstance(TaskInstance parentTaskInstance) {
		this.parentTaskInstanceId = parentTaskInstance.getId();
		this.parentTaskInstance = parentTaskInstance;

	}

	public void addCandidateGroup(GroupTo groupTo, IncludeExclusion includeExclusion) {
		addIdentityLink(null, groupTo.getGroupId(), groupTo.getGroupType(), includeExclusion, IdentityLinkType.candidate);
	}

	public void addCandidateUser(String userId, IncludeExclusion includeExclusion) {
		addIdentityLink(userId, null, null, includeExclusion, IdentityLinkType.candidate);
	}

	public void setTaskMgmtInstance(TaskMgmtInstance taskMgmtInstance) {
		this.taskMgmtInstance = taskMgmtInstance;
	}

	public void setTaskDefinition(TaskDefinition taskDefinition) {
		this.taskDefinition = taskDefinition;
	}

	public void setToken(TokenEntity token) {

		this.processInstanceId = token.getProcessInstance().getId();
		this.processDefinitionId = token.getProcessInstance().getProcessDefinition().getProcessDefinitionId();
		this.node = (UserTask) token.getFlowNode();
		this.nodeId = this.node.getId();
		this.nodeName = this.node.getName();
		this.tokenId = token.getId();
		this.token = token;
		this.bizKey = token.getProcessInstance().getBizKey();
		this.processDefinitionKey = token.getProcessInstance().getProcessDefinition().getProcessDefinitionKey();
		this.name = this.node.getName();
		this.processDefinitionName = token.getProcessInstance().getProcessDefinition().getName();

	}

	public void create(ExecutionContext executionContext) {
		if (createTime != null) {
			throw new FixFlowException("任务实例 '" + id + "' 已经被创建!", new Error());
		}
		createTime = new Date();

		// if this task instance is associated with a task...
		if ((taskDefinition != null) && (executionContext != null)) {
			//
			executionContext.setTaskInstance(this);
			executionContext.setTaskDefinition(taskDefinition);
			// task.fireEvent(Event.EVENTTYPE_TASK_CREATE, executionContext);
		}
	}

	public void start() {
		if (startTime != null) {
			throw new FixFlowException("任务实例 '" + id + "' 已经被标示为开始！");
		}

		startTime = new Date();
		/*
		 * if ((task != null) && (token != null)) { ExecutionContext
		 * executionContext = new ExecutionContext(token);
		 * executionContext.setTask(task);
		 * executionContext.setTaskInstance(this);
		 * task.fireEvent(Event.EVENTTYPE_TASK_START, executionContext); }
		 */
	}

	public void end(TaskCommandInst taskCommandInst, String taskComment) {

		// 设置任务上点击的处理命令
		this.setCommandId(taskCommandInst.getId());
		// 设置任务上点击的处理命令类型
		this.setCommandType(taskCommandInst.getTaskCommandType());
		// 设置任务上点击的处理命令文本
		this.setCommandMessage(taskCommandInst.getName());
		// 处理意见
		this.setTaskComment(taskComment);

		// 调用任务的完成方法
		end();
	}

	public void end() {

		// 设置是否为草稿
		this.isDraft = false;

		// this.operationCommand = operationCommand;
		if (this.endTime != null) {
			throw new FixFlowException("任务已经结束,不能再进行处理.");
		}
		if (this.isSuspended) {
			throw new FixFlowException("任务已经暂停不能再处理");
		}

		this.endTime = new Date();

		this.isOpen = false;

		// fire the task instance end event
		if ((taskDefinition != null) && (token != null)) {

			ExecutionContext executionContext = ProcessObjectFactory.FACTORYINSTANCE.createExecutionContext(token);
			executionContext.setTaskDefinition(taskDefinition);
			executionContext.setTaskInstance(this);
			// task.fireEvent(Event.EVENTTYPE_TASK_END, executionContext);
		}

		// log this assignment
		if (token != null) {
			// token.addLog(new TaskEndLog(this));
		}

		token.signal();

		// 用于并行、串行会签的处理.

		/*
		 * if (token.getFlowNode() instanceof UserTask && ((UserTask)
		 * this).getLoopCharacteristics() != null) {
		 * 
		 * UserTask userTask = (UserTask) token.getFlowNode();
		 * LoopCharacteristics loopCharacteristics =
		 * userTask.getLoopCharacteristics();
		 * 
		 * if (loopCharacteristics instanceof MultiInstanceLoopCharacteristics)
		 * { Set<TaskInstance>
		 * taskInstances=token.getProcessInstance().getTaskMgmtInstance
		 * ().getTaskInstances(token);
		 * 
		 * // 并行多实例处理 }else { // 串行处理 }
		 * 
		 * }
		 * 
		 * 
		 * 
		 * for (TaskInstance taskInstance :taskInstances) {
		 * if(taskInstance.getNodeId().equals(this.getNodeId())){
		 * 
		 * } }
		 */

	}

	public void toFlowNodeEnd(TaskCommandInst taskCommandInst, String taskComment, FlowNode flowNode, String rollBackAssignee) {

		// 分支退回处理

		if (token.getParent() == null) {
			// 主令牌非分支的处理

			customEnd(taskCommandInst, taskComment);
			ExecutionContext executionContext = ProcessObjectFactory.FACTORYINSTANCE.createExecutionContext(token);
			executionContext.setToFlowNode(flowNode);
			executionContext.setRollBackAssignee(rollBackAssignee);
			token.signal(executionContext);

		} else {
			// 非主令牌分支令牌的处理
			CommandExecutor commandExecutor = Context.getProcessEngineConfiguration().getCommandExecutor();
			TaskQuery taskQuery = new TaskQueryImpl(commandExecutor);
			Long taskNum = taskQuery.tokenId(token.getId()).nodeId(flowNode.getId()).count();
			if (taskNum != 0) {
				// 分支令牌经过这个节点则允许正常退回
				customEnd(taskCommandInst, taskComment);
				ExecutionContext executionContext = ProcessObjectFactory.FACTORYINSTANCE.createExecutionContext(token);
				executionContext.setToFlowNode(flowNode);
				executionContext.setRollBackAssignee(rollBackAssignee);
				token.signal(executionContext);
			} else {

				// 分支令牌经过这个节点则允许正常退回
				customEnd(taskCommandInst, taskComment);

				boolean isFind = toFlowNodeEnd(taskCommandInst, taskComment, flowNode, rollBackAssignee, token.getParent(), taskQuery);
				if (!isFind) {
					throw new FixFlowException("该节点从未到达过不能退回");
				}
			}

		}

	}

	private boolean toFlowNodeEnd(TaskCommandInst taskCommandInst, String taskComment, FlowNode flowNode, String rollBackAssignee,
			TokenEntity tokenObj, TaskQuery taskQuery) {
		Long taskNum = taskQuery.tokenId(tokenObj.getId()).nodeId(flowNode.getId()).count();
		if (taskNum != 0) {

			tokenObj.terminationChildToken();
			ExecutionContext executionContext = ProcessObjectFactory.FACTORYINSTANCE.createExecutionContext(tokenObj);
			executionContext.setToFlowNode(flowNode);
			executionContext.setRollBackAssignee(rollBackAssignee);
			tokenObj.signal(executionContext);
			return true;
		} else {
			if (tokenObj.getParent() != null) {
				return toFlowNodeEnd(taskCommandInst, taskComment, flowNode, rollBackAssignee, tokenObj.getParent(), taskQuery);

			} else {
				return false;
			}

		}

	}

	// public void customEnd(String taskCommandType,String
	// taskCommandName,FlowNode flowNode ) {

	// }

	/**
	 * 这个结束并不会去推动令牌向下。例如用在退回的时候。
	 */
	public void customEnd(TaskCommandInst taskCommandInst, String taskComment) {

		// this.operationCommand = operationCommand;
		if (this.endTime != null) {
			// throw new FixFlowException("任务已经结束,不能再进行处理.");
		}
		if (this.isSuspended) {
			throw new FixFlowException("任务已经暂停不能再处理");
		}

		this.endTime = new Date();

		this.isDraft = false;

		this.isOpen = false;

		this.taskComment = taskComment;

		if (taskCommandInst != null && taskCommandInst.getTaskCommandType() != null && !taskCommandInst.getTaskCommandType().equals("")) {
			String taskCommandType = taskCommandInst.getTaskCommandType();
			String taskCommandName = taskCommandInst.getName();
			// 设置流程自动结束信息 autoEnd
			this.setCommandId(taskCommandInst.getId());
			this.setCommandType(taskCommandType);
			if (taskCommandName == null) {
				TaskCommandDef taskCommandDef = Context.getProcessEngineConfiguration().getTaskCommandDefMap().get(taskCommandType);
				if (taskCommandDef != null) {
					this.setCommandMessage(taskCommandDef.getName());
				}
			} else {
				this.setCommandMessage(taskCommandName);

			}

		} else {

			this.setCommandId(TaskCommandType.AUTOEND);
			this.setCommandType(TaskCommandType.AUTOEND);
			TaskCommandDef taskCommandDef = Context.getProcessEngineConfiguration().getTaskCommandDefMap().get(TaskCommandType.AUTOEND);
			if (taskCommandDef != null) {
				this.setCommandMessage(taskCommandDef.getName());
			}
		}

	}

	public TokenEntity removeTimeOutTask() {

		if (this.node.getBoundaryEventRefs().size() > 0) {
			// List<BoundaryEvent> boundaryEvents = this.getBoundaryEventRefs();
			TokenEntity tokenEntity = this.getToken();
			String parentTokenId = tokenEntity.getParent().getId();
			try {
				Scheduler scheduler = Context.getProcessEngineConfiguration().getSchedulerFactory().getScheduler();
				// Set<JobKey>
				// jobKeys=scheduler.getJobKeys(GroupMatcher.jobGroupContains("Out"));
				Map<String, TokenEntity> tokenBrothers = tokenEntity.getParent().getChildren();

				int colseTokenBrothersNum = 0;

				for (String tokenBrotherKey : tokenBrothers.keySet()) {

					if (!tokenBrotherKey.equals(tokenEntity.getId())) {

						// for (BoundaryEvent boundaryEvent :
						// this.node.getBoundaryEventRefs()) {
						// if(tokenBrothers.get(tokenBrotherKey).getFlowNode().getId().equals(boundaryEvent.getId())){
						// JobDetail
						// JobDetail=scheduler.getJobDetail(JobKey.jobKey(tokenBrothers.get(tokenBrotherKey).getId(),"FixTimeOutTask_"+parentTokenId));
						scheduler.deleteJob(JobKey.jobKey(tokenBrothers.get(tokenBrotherKey).getId(), "FixTimeOutTask_" + parentTokenId));
						tokenBrothers.get(tokenBrotherKey).end();
						colseTokenBrothersNum = colseTokenBrothersNum + 1;
						break;
						// }
						// }
					}
				}

				if (colseTokenBrothersNum == (tokenBrothers.keySet().size() - 1)) {
					tokenEntity.getParent().terminationChildToken();
					// ExecutionContext
					// executionContextParent=ProcessObjectFactory.FACTORYINSTANCE.createExecutionContext(tokenEntity.getParent());

					// super.leave(executionContextParent, sequenceFlow);
					return tokenEntity.getParent();
				}

			} catch (SchedulerException e) {
				e.printStackTrace();
				throw new FixFlowException("流程在离开节点 " + this.getId() + " 的时候发生错误! 错误信息: " + e.toString(), e);
			}

		}

		return null;
	}

	public void assign(ExecutionContext executionContext) {

		// 这里会有泳道的处理 暂时没有实现泳道

		TaskMgmtInstance taskMgmtInstance = executionContext.getTaskMgmtInstance();

		taskMgmtInstance.performAssignment(taskDefinition, this, executionContext);

	}

	public IdentityLinkEntity addIdentityLink(String userId, String groupId, String groupType, IncludeExclusion includeExclusion,
			IdentityLinkType type) {
		IdentityLinkEntity identityLink = new IdentityLinkEntity(GuidUtil.CreateGuid());

		identityLink.setTaskInstance(this);
		identityLink.setUserId(userId);
		identityLink.setGroupId(groupId);
		identityLink.setGroupType(groupType);
		identityLink.setType(type);
		identityLink.setIncludeExclusion(includeExclusion);
		this.taskIdentityLinks.add(identityLink);
		return identityLink;
	}

	/**
	 * 暂停任务
	 */
	public void suspend() {
		isSuspended = true;
		isOpen = false;
	}

	/**
	 * 恢复任务
	 */
	public void resume() {
		isSuspended = false;
		isOpen = true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<IdentityLink> getTaskIdentityLinks() {

		return (List) taskIdentityLinks;
	}

	public List<IdentityLinkEntity> getTaskIdentityLinkEntitys() {

		return taskIdentityLinks;
	}

	public boolean hasEnded() {
		return (endTime != null);
	}

	public void cancel() {
		isCancelled = true;
	}

	public void setAssigneeId(String assigneeId) {
		this.assignee = assigneeId;
	}

	public void setOwnerId(String ownerId) {
		this.owner = ownerId;
	}

	// 持久化的时候用的方法

	public void setAssigneeWithoutCascade(String assignee) {
		this.assignee = assignee;
	}

	public void setOwnerWithoutCascade(String owner) {
		this.owner = owner;
	}

	public void setDueDateWithoutCascade(Date dueDate) {
		this.dueDate = dueDate;
	}

	public void setPriorityWithoutCascade(int priority) {
		this.priority = priority;
	}

	public void setParentTaskIdWithoutCascade(String parentTaskInstanceId) {
		this.parentTaskInstanceId = parentTaskInstanceId;
	}

	public void setNameWithoutCascade(String taskName) {
		this.name = taskName;
	}

	public void setDescriptionWithoutCascade(String description) {
		this.description = description;
	}

	public void setEndTimeWithoutCascade(Date endTime) {
		this.endTime = endTime;
	}

	public void setClaimTimeWithoutCascade(Date claimTime) {
		this.claimTime = claimTime;
	}

	public void setIdWithoutCascade(String id) {
		this.id = id;
	}

	public void setCreateTimeWithoutCascade(Date createTime) {
		this.createTime = createTime;
	}

	public void setStartTimeWithoutCascade(Date startTime) {
		this.startTime = startTime;
	}

	public void setProcessDefinitionKeyWithoutCascade(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}

	public void setProcessDefinitionIdWithoutCascade(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<IdentityLink> getIdentityLinkQueryToListNoCache() {

		if (this.taskIdentityLinks.size() == 0) {

			List valueObjectTemp = (List) Context.getCommandContext().getIdentityLinkManager().findIdentityLinksByTaskId(this.id);
			if (valueObjectTemp.size() > 0) {

				this.taskIdentityLinks = (List<IdentityLinkEntity>) valueObjectTemp;
				return (List) this.taskIdentityLinks;
			} else {
				return (List) this.taskIdentityLinks;
			}

		} else {
			return (List) this.taskIdentityLinks;
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<IdentityLink> getIdentityLinkQueryToList() {

		if (this.taskIdentityLinks.size() == 0) {

			CacheHandler cacheHandler = Context.getProcessEngineConfiguration().getCacheHandler();

			Object valueObject = cacheHandler.getCacheData("IdentityLink_" + this.id);
			if (valueObject != null) {
				this.taskIdentityLinks = (List<IdentityLinkEntity>) valueObject;
				return (List) this.taskIdentityLinks;
			} else {
				List valueObjectTemp = (List) Context.getCommandContext().getIdentityLinkManager().findIdentityLinksByTaskId(this.id);
				if (valueObjectTemp.size() > 0) {
					cacheHandler.putCacheData("IdentityLink_" + this.id, valueObjectTemp);
					this.taskIdentityLinks = (List<IdentityLinkEntity>) valueObjectTemp;
					return (List) this.taskIdentityLinks;
				} else {
					return (List) this.taskIdentityLinks;
				}
			}
		} else {
			return (List) this.taskIdentityLinks;
		}

	}

	public ProcessDefinitionBehavior getProcessDefinition() {

		if (this.processDefinitionId == null || this.processDefinitionId.equals("")) {
			throw new FixFlowException("processDefinitionId 不能为空!");
		}
		return Context.getCommandContext().getProcessDefinitionManager().findLatestProcessDefinitionById(this.processDefinitionId);
	}

	public Object getExtensionField(String fieldName) {
		return extensionFields.get(fieldName);
	}

	public Map<String, Object> getExtensionFields() {
		return extensionFields;
	}

	public void setExtensionFields(Map<String, Object> extensionFields) {
		this.extensionFields = extensionFields;
	}

	public void addExtensionField(String fieldName, Object fieldValue) {
		this.extensionFields.put(fieldName, fieldValue);
	}

	/**
	 * 从数据库读取任务
	 */
	public TaskInstanceEntity(Map<String, Object> entityMap) {

		persistentInit(entityMap);

	}

	public void persistentInit(Map<String, Object> entityMap) {
		for (String dataKey : entityMap.keySet()) {

			if (dataKey.equals(TaskInstanceObjKey.TaskInstanceId().DataBaseKey())) {
				this.setId(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.Agent().DataBaseKey())) {
				this.setAgent(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.Name().DataBaseKey())) {
				this.setNameWithoutCascade(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.Description().DataBaseKey())) {
				this.setDescriptionWithoutCascade(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.Priority().DataBaseKey())) {
				this.setPriorityWithoutCascade(StringUtil.getInt(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.Owner().DataBaseKey())) {
				this.setOwnerWithoutCascade(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.Assignee().DataBaseKey())) {
				this.setAssigneeWithoutCascade(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.NodeId().DataBaseKey())) {
				this.setNodeId(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.ProcessInstanceId().DataBaseKey())) {
				this.setProcessInstanceId(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.ProcessDefinitionId().DataBaseKey())) {
				this.setProcessDefinitionIdWithoutCascade(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.ProcessDefinitionKey().DataBaseKey())) {
				this.setProcessDefinitionKeyWithoutCascade(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.DelegationState().DataBaseKey())) {

				if (entityMap.get(dataKey) != null) {
					this.setDelegationState(DelegationState.valueOf(entityMap.get(dataKey).toString()));

				}
				continue;
			}
			if (dataKey.equals(TaskInstanceObjKey.TokenId().DataBaseKey())) {
				this.setTokenId(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.CreateTime().DataBaseKey())) {
				this.setCreateTime(StringUtil.getDate(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.StartTime().DataBaseKey())) {
				this.setStartTime(StringUtil.getDate(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.EndTime().DataBaseKey())) {
				this.setEndTime(StringUtil.getDate(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.DueDate().DataBaseKey())) {
				this.setDueDateWithoutCascade(StringUtil.getDate(entityMap.get(dataKey)));
				continue;
			}
			if (dataKey.equals(TaskInstanceObjKey.ClaimTime().DataBaseKey())) {
				this.setClaimTime(StringUtil.getDate(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.BizKey().DataBaseKey())) {
				this.setBizKey(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.CommandId().DataBaseKey())) {
				this.setCommandId(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.CommandType().DataBaseKey())) {
				this.setCommandType(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.CommandMessage().DataBaseKey())) {
				this.setCommandMessage(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.TaskComment().DataBaseKey())) {
				this.setTaskComment(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}
			if (dataKey.equals(TaskInstanceObjKey.NodeName().DataBaseKey())) {
				this.setNodeName(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}
			if (dataKey.equals(TaskInstanceObjKey.FormUri().DataBaseKey())) {
				this.setFormUri(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}
			if (dataKey.equals(TaskInstanceObjKey.FormUriView().DataBaseKey())) {
				this.setFormUriView(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}
			if (dataKey.equals(TaskInstanceObjKey.TaskGroup().DataBaseKey())) {
				this.setTaskGroup(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}
			if (dataKey.equals(TaskInstanceObjKey.TaskInstanceType().DataBaseKey())) {
				this.setTaskInstanceType(TaskInstanceType.valueOf(StringUtil.getString(entityMap.get(dataKey))));
				continue;
			}
			if (dataKey.equals(TaskInstanceObjKey.ProcessDefinitionName().DataBaseKey())) {
				this.setProcessDefinitionName(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.IsDraft().DataBaseKey())) {
				this.setDraft(StringUtil.getBoolean(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.IsOpen().DataBaseKey())) {
				this.isOpen = StringUtil.getBoolean(entityMap.get(dataKey));
				continue;
			}
			if (dataKey.equals(TaskInstanceObjKey.IsSuspended().DataBaseKey())) {
				this.isSuspended = StringUtil.getBoolean(entityMap.get(dataKey));
				continue;
			}
			if (dataKey.equals(TaskInstanceObjKey.IsCancelled().DataBaseKey())) {
				this.isCancelled = StringUtil.getBoolean(entityMap.get(dataKey));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.Category().DataBaseKey())) {
				this.setCategory(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.ExpectedExecutionTime().DataBaseKey())) {
				this.setExpectedExecutionTime(StringUtil.getInt(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.Admin().DataBaseKey())) {
				this.setAdmin(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.CallActivityInstanceId().DataBaseKey())) {
				this.setCallActivityInstanceId(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}

			if (dataKey.equals(TaskInstanceObjKey.PendingTaskId().DataBaseKey())) {
				this.setPendingTaskId(StringUtil.getString(entityMap.get(dataKey)));
				continue;
			}
			if (dataKey.equals(TaskInstanceObjKey.ArchiveTime().DataBaseKey())) {
				this.setArchiveTime(StringUtil.getDate(entityMap.get(dataKey)));
				continue;
			}

			this.addExtensionField(dataKey, entityMap.get(dataKey));
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getPersistentDbMap() {
		Map<String, Object> objectParam = new HashMap<String, Object>();

		AbstractScriptLanguageMgmt scriptLanguageMgmt=Context.getAbstractScriptLanguageMgmt();
		
		objectParam=(Map<String, Object>)scriptLanguageMgmt.executeBusinessRules(GET_TASKINSTANCE_PERSISTENT_DBMAP, this);


		return objectParam;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getPersistentState() {

		
		Map<String, Object> persistentState =null;
		
		AbstractScriptLanguageMgmt scriptLanguageMgmt=Context.getAbstractScriptLanguageMgmt();
		
		persistentState=(Map<String, Object>)scriptLanguageMgmt.executeBusinessRules(GET_TASKINSTANCE_PERSISTENT_STATE, this);

		
		return persistentState;
	}

	public TaskInstanceEntity clone() {

		TaskInstanceEntity taskInstanceEntityNew = new TaskInstanceEntity();
		taskInstanceEntityNew.persistentInit(getPersistentDbMap());

		return taskInstanceEntityNew;
	}

}
