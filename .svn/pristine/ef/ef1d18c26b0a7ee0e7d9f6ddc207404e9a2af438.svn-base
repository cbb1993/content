package com.huanhong.content.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.huanhong.content.R;

import java.io.File;

public class IntentUtils {

	private Intent getHtmlFileIntent(File file) {
		Uri uri = Uri.parse(file.toString()).buildUpon()
				.encodedAuthority("com.android.htmlfileprovider")
				.scheme("content").encodedPath(file.toString()).build();
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.setDataAndType(uri, "text/html");
		return intent;
	}

	// android pic
	private Intent getImageFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "image/*");
		return intent;
	}

	// android pdf
	private Intent getPdfFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/pdf");
		return intent;
	}

	// android txt
	private Intent getTextFileIntent(File file) {

//		if (file.length() >= 1024 * 100) {
			Intent intent = new Intent("android.intent.action.VIEW");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Uri uri = Uri.fromFile(file);
			intent.setDataAndType(uri, "text/plain");
			return intent;
//		} else {
//			Intent intent = new Intent("action.com.dashuai.file.txt.view");
//			intent.addCategory("android.intent.category.DEFAULT");
//
//			intent.putExtra("filePath", file.getAbsolutePath());
//
//			return intent;
//		}

	}

	// android audio
	private Intent getAudioFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "audio/*");
		return intent;
	}

	// android vedio
	private Intent getVideoFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "video/*");
		return intent;
	}

	// android word
	private Intent getWordFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/msword");
		return intent;
	}

	// android Excel
	private Intent getExcelFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/vnd.ms-excel");
		return intent;
	}

	// android PPT
	private Intent getPPTFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		return intent;
	}

	// android CHM
	private Intent getChmFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/x-chm");
		return intent;
	}

	// android apk
	private Intent getApkFileIntent(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		return intent;
	}

	private String[] fileEndingImage;
	private String[] fileEndingAudio;
	private String[] fileEndingVideo;
	private String[] fileEndingWebText;
	private String[] fileEndingText;
	private String[] fileEndingWord;
	private String[] fileEndingExcel;
	private String[] fileEndingPPT;
	private String[] fileEndingPdf;

	private IntentUtils(Context context) {
		fileEndingImage = context.getResources().getStringArray(
				R.array.fileEndingImage);
		fileEndingAudio = context.getResources().getStringArray(
				R.array.fileEndingAudio);
		fileEndingVideo = context.getResources().getStringArray(
				R.array.fileEndingVideo);
		fileEndingWebText = context.getResources().getStringArray(
				R.array.fileEndingWebText);
		fileEndingText = context.getResources().getStringArray(
				R.array.fileEndingText);
		fileEndingWord = context.getResources().getStringArray(
				R.array.fileEndingWord);
		fileEndingExcel = context.getResources().getStringArray(
				R.array.fileEndingExcel);
		fileEndingPPT = context.getResources().getStringArray(
				R.array.fileEndingPPT);
		fileEndingPdf = context.getResources().getStringArray(
				R.array.fileEndingPdf);
	}

	public static IntentUtils utils;

	public static IntentUtils getInstance(Context context) {

		if (null == utils) {
			utils = new IntentUtils(context);
		}

		return utils;
	}

	public Intent openFile(File file) {

		String path = file.getAbsolutePath();

		int index = path.lastIndexOf(".");

		if (-1 != index) {
			String suffix = path.substring(index);

			if (suffix.equals(".apk")) {
				return getApkFileIntent(file);
			} else if (suffix.equals(".chm")) {
				return getChmFileIntent(file);
			} else if (isMatchSuffix(suffix, fileEndingImage)) {
				return getImageFileIntent(file);
			} else if (isMatchSuffix(suffix, fileEndingAudio)) {
				return getAudioFileIntent(file);
			} else if (isMatchSuffix(suffix, fileEndingVideo)) {
				return getVideoFileIntent(file);
			} else if (isMatchSuffix(suffix, fileEndingText)) {
				return getTextFileIntent(file);
			} else if (isMatchSuffix(suffix, fileEndingWebText)) {
				return getHtmlFileIntent(file);
			} else if (isMatchSuffix(suffix, fileEndingWord)) {
				return getWordFileIntent(file);
			} else if (isMatchSuffix(suffix, fileEndingExcel)) {
				return getExcelFileIntent(file);
			} else if (isMatchSuffix(suffix, fileEndingPPT)) {
				return getPPTFileIntent(file);
			} else if (isMatchSuffix(suffix, fileEndingPdf)) {
				return getPdfFileIntent(file);
			}

		}

		return null;
	}

	private boolean isMatchSuffix(String suffix, String[] endings) {

		for (String s : endings) {
			if (s.equalsIgnoreCase(suffix)) {
				return true;
			}
		}
		return false;
	}

}
