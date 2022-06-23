package com.ManagSystem.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ManagSystem.entity.TaskEntity;
import com.ManagSystem.form.TaskDetailForm;
import com.ManagSystem.form.TaskRegisterForm;
import com.ManagSystem.form.TaskSearchForm;
import com.ManagSystem.form.TaskUpdateForm;
import com.ManagSystem.service.TaskService;
import com.ManagSystem.util.Common;

@Controller
@RequestMapping("/task")
public class TaskController {
	
	@Autowired
	private TaskService TaskService;
	
	@Autowired
	HttpSession session;
	
	// タスク新規登録初期表示
	@RequestMapping(value="/register/init", method = RequestMethod.GET)
	public String initRegister(Model model, Principal principal) {
		// セッションからメッセージ取得
		String info = (String) session.getAttribute("infoMessage");
		// メッセージがある場合画面に表示
		if(!Common.isEmpty(info)) {
			model.addAttribute("infoMessage", info);
		}
		// セッションクリア
		session.removeAttribute("errorMessage");
		session.removeAttribute("infoMessage");
		
		// 入力フォームの初期化
		TaskRegisterForm taskResisterForm = new TaskRegisterForm();
		// 入力項目の反映
		model.addAttribute("taskRegisterForm", taskResisterForm);
		
		// 新規登録画面に遷移
		return "task/task-register";
		
	}
	
	// タスク新規登録処理
	@RequestMapping(value="/register", method = RequestMethod.POST)
	public String update(@Validated @ModelAttribute TaskRegisterForm task, BindingResult bindingResult, Model model) {
		// セッションクリア
		session.removeAttribute("errorMessage");
		session.removeAttribute("infoMessage");
		// エラーが1件でもある場合
		if (bindingResult.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
			for(ObjectError error : bindingResult.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			model.addAttribute("errorMessage", errorList);
			// 新規登録画面へ戻る
			return "task/task-register";
		}
		// DB登録処理
		TaskService.save(task);
		// セッションに完了メッセージを格納
		session.setAttribute("infoMessage", "タスクを登録しました");
		// 新規登録画面へ遷移
		return "redirect:/task/register/init";
	}
	
	//タスク検索一覧初期表示
	@RequestMapping(value="/search/init", method = RequestMethod.GET)
	public String getSearchView(Model model) {
		// セッションクリア
		//session.invalidate();
		session.removeAttribute("errorMessage");
		session.removeAttribute("infoMessage");
		// 検索項目入力フォームの初期化
		TaskSearchForm taskSearchForm = new TaskSearchForm();
		// 検索一覧項目の初期化
		List<TaskEntity> task = null;
		// 一覧項目に反映
		model.addAttribute("task", task);
		// 検索項目に反映
		model.addAttribute("taskSearchForm", taskSearchForm);
		// 検索一覧画面へ遷移
		return "task/task-list";
	}
	
	// タスク検索処理
	@RequestMapping(value="/search/list", method = RequestMethod.POST)
	public String Search(@ModelAttribute("taskSearchForm") TaskSearchForm taskSearchForm, Model model) {
		// セッションクリア
		//session.invalidate();
		session.removeAttribute("errorMessage");
		session.removeAttribute("infoMessage");
		// セッションに検索条件を保持
		session.setAttribute("taskSearchForm", taskSearchForm);
		// DB検索処理
		List<TaskEntity> task = TaskService.listSearch(taskSearchForm);
		// 検索結果を一覧へ反映
		model.addAttribute("task", task);
		// 検索一覧画面を再表示
		return "task/task-list";
	}
	
	// タスク詳細画面遷移
	@RequestMapping(value="/detail", method = RequestMethod.POST)
	public String detail(@RequestParam("taskId")String taskId, Model model) {
		// 一覧で選択したエンジニアIDより詳細データを取得
		TaskDetailForm  task = TaskService.detailSearch(taskId);
		// 詳細画面項目へ反映
		model.addAttribute("task",task);
		// 詳細画面へ遷移
		return "task/task-detail";
	}
	
	// タスク削除
	@RequestMapping(value="/delete", method = RequestMethod.POST)
	public String delete(@RequestParam("taskId")String taskId, Model model) {
		// タスクデータの削除
		TaskService.delete(taskId);
		// 正常に削除できた場合は一覧画面へ戻る
		TaskSearchForm taskSearchForm = new TaskSearchForm();
		// セッションから検索条件を取得
		taskSearchForm = (TaskSearchForm) session.getAttribute("taskSearchForm");
		// DB検索処理
		List<TaskEntity> task = TaskService.listSearch(taskSearchForm);
		// 検索項目に反映
		model.addAttribute("taskSearchForm", taskSearchForm);
		// 一覧項目に反映
		model.addAttribute("task", task);
		// 変更完了メッセージを表示する
		model.addAttribute("infoMessage", "タスクを削除しました");
		// 検索一覧へ遷移
		return "task/task-list";
	}

	// タスク変更画面表示
	@RequestMapping(value="/update", method = RequestMethod.POST)
	public String update(@RequestParam("taskId")String taskId, Model model) {
		// 変更対象のエンジニアマスタデータを抽出
		TaskUpdateForm taskUpdateForm = TaskService.updateSearch(taskId);
		// 画面項目に反映
		model.addAttribute("taskUpdateForm",taskUpdateForm);
		// 変更画面へ遷移
		return "task/task-update";
	}
	
	// タスク変更処理
	@RequestMapping(value="/update/save", method = RequestMethod.POST)
	public String save(@Validated @ModelAttribute("taskUpdateForm") TaskUpdateForm taskUpdateForm, BindingResult bindingResult, Model model) {
		// 属性チェックにて1件でもエラーがある場合
		if (bindingResult.hasErrors()) {
			List<String> errorList = new ArrayList<String>();
			for(ObjectError error : bindingResult.getAllErrors()) {
				errorList.add(error.getDefaultMessage());
			}
			model.addAttribute("errorMessage", errorList);
			return "task/task-update";
		}
		// エンジニアマスタデータを変更
		TaskService.update(taskUpdateForm);
		// 変更対象のエンジニアマスタデータを抽出
		taskUpdateForm = TaskService.updateSearch(taskUpdateForm.getTaskId());
		// 画面項目に反映
		model.addAttribute("taskUpdateForm",taskUpdateForm);
		// 変更完了メッセージを表示する
		model.addAttribute("infoMessage", "タスクの内容を変更しました");
		// 変更画面を再表示
		return "task/task-update";
	}
	// 検索一覧に戻る
	@RequestMapping(value="/returnList", method = RequestMethod.POST)
	public String returnList(Model model) {
		// 正常に削除できた場合は一覧画面へ戻る
		TaskSearchForm taskSearchForm = new TaskSearchForm();
		// セッションから検索条件を取得
		taskSearchForm = (TaskSearchForm) session.getAttribute("taskSearchForm");
		// 検索項目を設定
		model.addAttribute("taskSearchForm", taskSearchForm);
		// 一覧表示データを検索
		List<TaskEntity> task = TaskService.listSearch(taskSearchForm);
		// 一覧項目を設定
		model.addAttribute("task", task);
		// 検索一覧へ遷移
		return "task/task-list";
	}
}
