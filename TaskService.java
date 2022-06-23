package com.ManagSystem.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ManagSystem.entity.TaskEntity;
import com.ManagSystem.form.TaskDetailForm;
import com.ManagSystem.form.TaskRegisterForm;
import com.ManagSystem.form.TaskSearchForm;
import com.ManagSystem.form.TaskUpdateForm;
import com.ManagSystem.repository.TaskRepository;
import com.ManagSystem.util.Common;

@Service
@Transactional(rollbackOn = Exception.class)
public class TaskService {

	@Autowired
	TaskRepository taskRepository;

	// タスク全件取得
	public List<TaskEntity> searchAll() {
		// タスク全検索
		List<TaskEntity> dataList = taskRepository.findAll();
		return dataList;
	}

	// タスク新規登録処理
	public TaskEntity save(TaskRegisterForm form) {
		// エンティティを初期化
		TaskEntity task = new TaskEntity();
		// タスクIDの採番
		int checkCount = searchAll().size();
		String taskId = null;
		if (checkCount <= 0) {
			taskId = "0001";
		} else {
			int dataCount = taskRepository.getTaskIDMaxValue() + 1;
			taskId = "" + String.format("%04d", dataCount);
		}
		// 入力フォームのデータをエンティティに設定
		task.setTaskId(taskId);
		task.setTaskTitle(form.getTaskTitle());
		task.setTaskKana(form.getTaskKana());
		task.setTaskMember(form.getTaskMember());
		task.setTaskMemberKana(form.getTaskMemberKana());
		task.setTaskStartDate(form.getTaskStartDate());
		task.setTaskFinishDate(form.getTaskFinishDate());
		task.setTaskPriority(form.getTaskPriority());
		task.setTaskProgress(form.getTaskProgress());
		task.setTaskDetail(form.getTaskDetail());
		// 登録処理実行
		TaskEntity result = taskRepository.save(task);
		return result;
	}

	// タスク(一覧)検索処理
	public List<TaskEntity> listSearch(TaskSearchForm taskSearchform) {
		// 検索結果を初期化
		List<TaskEntity> result = new ArrayList<TaskEntity>();
		// 検索処理実行
		result = taskRepository.findAll(Specification.where(
				// タスク名(部分一致)
				Common.spec(TaskEntity.class, (cb, root) -> {
					// 入力された文字がひらがなか判別
					String kana = taskSearchform.getTaskTitle();
					if (kana.matches("^[\\u3040-\\u309F]+$")) {
						// カタカナだった場合タスクかな検索(部分一致)
						taskSearchform.setTaskKana(taskSearchform.getTaskTitle());
						if (!Common.isEmpty(taskSearchform.getTaskKana())) {
							return cb.like(root.get("taskKana"), "%" + taskSearchform.getTaskKana() + "%");
						}
					} else {
						// 漢字だった場合タスク検索(部分一致)
						if (!Common.isEmpty(taskSearchform.getTaskTitle())) {
							return cb.like(root.get("taskTitle"), "%" + taskSearchform.getTaskTitle() + "%");
						}
					}
					return null;
				})).and(Common.spec(TaskEntity.class, (cb, root) -> {
					// 入力された文字がひらがなか判別
					String kana = taskSearchform.getTaskMember();
					if (kana.matches("^[\\u3040-\\u309F]+$")) {
						// カタカナだった場合タスクかな検索(部分一致)
						taskSearchform.setTaskMemberKana(taskSearchform.getTaskMember());
						if (!Common.isEmpty(taskSearchform.getTaskMemberKana())) {
							return cb.like(root.get("taskMemberKana"), "%" + taskSearchform.getTaskMemberKana() + "%");
						}
					} else {
						// 漢字だった場合タスク検索(部分一致)
						if (!Common.isEmpty(taskSearchform.getTaskMember())) {
							return cb.like(root.get("taskMember"), "%" + taskSearchform.getTaskMember() + "%");
						}
					}
					return null;
				})).and(
						// 日付(部分一致)
						Common.spec(TaskEntity.class, (cb, root) -> {
							if (!Common.isEmpty(taskSearchform.getTaskStartDate())) {
								return cb.like(root.get("taskStartDate"), "%" + taskSearchform.getTaskStartDate() + "%");
							} else {
								return null;
							}

						}))
				.and(
						// 期限日(部分一致)
						Common.spec(TaskEntity.class, (cb, root) -> {
							if (!Common.isEmpty(taskSearchform.getTaskFinishDate())) {
								return cb.like(root.get("taskFinishDate"), "%" + taskSearchform.getTaskFinishDate() + "%");
							} else {
								return null;
							}

						})));
		return result;
	}
	

	// タスク(詳細)検索処理
	public TaskDetailForm detailSearch(String taskId) {
		// 一覧で選択したタスクIDの詳細データを取得
		List<TaskEntity> result = taskRepository.findByTaskId(taskId);
		// 詳細画面の各項目に詳細データを設定
		TaskDetailForm form = new TaskDetailForm();
		form.setTaskId(result.get(0).getTaskId());
		form.setTaskTitle(result.get(0).getTaskTitle());
		form.setTaskKana(result.get(0).getTaskKana());
		form.setTaskMember(result.get(0).getTaskMember());
		form.setTaskMemberKana(result.get(0).getTaskMemberKana());
		form.setTaskStartDate(result.get(0).getTaskStartDate());
		form.setTaskFinishDate(result.get(0).getTaskFinishDate());
		form.setTaskPriority(result.get(0).getTaskPriority());
		form.setTaskProgress(result.get(0).getTaskProgress());
		form.setTaskDetail(result.get(0).getTaskDetail());
		return form;
	}

	// タスク(変更)検索処理
	public TaskUpdateForm updateSearch(String taskId) {
		// 一覧で選択したタスクIDの詳細データを取得
		List<TaskEntity> result = taskRepository.findByTaskId(taskId);
		// 詳細画面の各項目に詳細データを設定
		TaskUpdateForm form = new TaskUpdateForm();
		form.setTaskId(result.get(0).getTaskId());
		form.setTaskTitle(result.get(0).getTaskTitle());
		form.setTaskKana(result.get(0).getTaskKana());
		form.setTaskMember(result.get(0).getTaskMember());
		form.setTaskMemberKana(result.get(0).getTaskMemberKana());
		form.setTaskStartDate(result.get(0).getTaskStartDate());
		form.setTaskFinishDate(result.get(0).getTaskFinishDate());
		form.setTaskPriority(result.get(0).getTaskPriority());
		form.setTaskProgress(result.get(0).getTaskProgress());
		form.setTaskDetail(result.get(0).getTaskDetail());
		return form;
	}

	// タスク削除処理
	public void delete(String taskId) {
		// 処理対象のタスクデータを取得
		List<TaskEntity> result = taskRepository.findByTaskId(taskId);
		TaskEntity entity = result.get(0);
		// 削除処理を実行
		taskRepository.delete(entity);
	}

	// タスク更新処理
	public TaskEntity update(TaskUpdateForm form) {
		// エンティティを初期化
		TaskEntity entity = new TaskEntity();
		// 入力フォームのデータをエンティティに設定
		entity.setTaskId(form.getTaskId());
		entity.setTaskTitle(form.getTaskTitle());
		entity.setTaskKana(form.getTaskKana());
		entity.setTaskMember(form.getTaskMember());
		entity.setTaskMemberKana(form.getTaskMemberKana());
		entity.setTaskStartDate(form.getTaskStartDate());
		entity.setTaskFinishDate(form.getTaskFinishDate());
		entity.setTaskPriority(form.getTaskPriority());
		entity.setTaskProgress(form.getTaskProgress());
		entity.setTaskDetail(form.getTaskDetail());
		// 更新処理を実行
		return taskRepository.save(entity);
	}

}
