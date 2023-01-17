package com.speno.xedm.gui.frontend.client.panels;

import java.util.ArrayList;
import java.util.List;

import org.moxieapps.gwt.uploader.client.Uploader;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteEvent;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteHandler;
import org.moxieapps.gwt.uploader.client.events.FileDialogStartEvent;
import org.moxieapps.gwt.uploader.client.events.FileDialogStartHandler;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorEvent;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorHandler;
import org.moxieapps.gwt.uploader.client.events.FileQueuedEvent;
import org.moxieapps.gwt.uploader.client.events.FileQueuedHandler;
import org.moxieapps.gwt.uploader.client.events.UploadCompleteEvent;
import org.moxieapps.gwt.uploader.client.events.UploadCompleteHandler;
import org.moxieapps.gwt.uploader.client.events.UploadErrorEvent;
import org.moxieapps.gwt.uploader.client.events.UploadErrorHandler;
import org.moxieapps.gwt.uploader.client.events.UploadProgressEvent;
import org.moxieapps.gwt.uploader.client.events.UploadProgressHandler;
import org.moxieapps.gwt.uploader.client.events.UploadSuccessEvent;
import org.moxieapps.gwt.uploader.client.events.UploadSuccessHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Progressbar;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.Util;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CopyOfFileDropArea extends HLayout {
	
	private com.smartgwt.client.widgets.Label progressLabel;
	private VLayout progressBarAndButtonPanel;
	private Progressbar progressBar = null;
	private HLayout dropAreaPanel;
	private Uploader uploader = new Uploader();
	private List<String> fileIdList = new ArrayList<String>();
	
	// upload 완료시 호출할 리스너
	private ReturnHandler completeHandler;

	private int width = 200;
	private int height = 30;
	
	private boolean hasError = false;
	
	private void updateUrl() {
		String url = GWT.getHostPageBaseURL() + "upload-directly?sid=" + Session.get().getSid();
		url += "&folderId=" + Session.get().getCurrentFolder().getId();
		uploader.setUploadURL(url);
	}
	
	public CopyOfFileDropArea(int width, int height) {
		this.width = width;
		this.height = height;
		
		String maxSize = Session.get().getInfo().getConfig("upload.maxsize");
		if (maxSize.length() > 0) {
			maxSize += " MB";
		}
		else
			maxSize = "100 MB";
		
		uploader
				.setButtonImageURL(ItemFactory.newImgIcon("add.png").getSrc())
				//.setButtonText(I18N.message("DirectUpload"))
				.setButtonWidth(16)
				.setButtonHeight(16)
//				.setButtonText("INPUT")
				.setFileSizeLimit(maxSize)
//				.setButtonCursor(Uploader.Cursor.HAND)
				.setButtonAction(Uploader.ButtonAction.SELECT_FILES)
				.setFileQueuedHandler(new FileQueuedHandler() {
					public boolean onFileQueued(final FileQueuedEvent fileQueuedEvent) {
						updateUrl();
						
						fileIdList.add(fileQueuedEvent.getFile().getId());
						
						showProgress();
						
						return true;
					}
				})
				.setUploadSuccessHandler(new UploadSuccessHandler() {
					@Override
					public boolean onUploadSuccess(UploadSuccessEvent uploadSuccessEvent) {
						return true;
					}
				})
				.setUploadProgressHandler(new UploadProgressHandler() {
					public boolean onUploadProgress(UploadProgressEvent uploadProgressEvent) {
//						uploadedSize += uploadProgressEvent.getBytesComplete();
						int percent = (int) (100 * uploadProgressEvent.getBytesComplete() / uploadProgressEvent.getBytesTotal());
						if (percent < 95) {
							String str = I18N.message("uploading") + ".." + "(" + uploadProgressEvent.getFile().getName() + ")";
							progressLabel.setContents(Util.strCut(str, 30, ".."));
							progressLabel.setTooltip(uploadProgressEvent.getFile().getName());
						}
						else {
							progressLabel.setContents(I18N.message("importing") + "..");
						}

						progressBar.setPercentDone(percent);
						
						return true;
					}
				})
				.setUploadCompleteHandler(new UploadCompleteHandler() {
					public boolean onUploadComplete(UploadCompleteEvent uploadCompleteEvent) {
//						System.out.println("@ complete : " + uploadCompleteEvent.getFile().getName());
						showDropPanelIfCompleted(uploadCompleteEvent.getFile().getId());
						
						uploader.startUpload();		// 다음 파일을 시작하기 위해 필수임.
						return true;
					}
				})
				.setFileDialogStartHandler(new FileDialogStartHandler() {
					public boolean onFileDialogStartEvent(FileDialogStartEvent fileDialogStartEvent) {
						if (uploader.getStats().getUploadsInProgress() <= 0) {
							fileIdList.clear();
						}

						updateUrl();

						return true;
					}
				})
				.setFileDialogCompleteHandler(new FileDialogCompleteHandler() {
					public boolean onFileDialogComplete(FileDialogCompleteEvent fileDialogCompleteEvent) {
						if (fileDialogCompleteEvent.getTotalFilesInQueue() > 0) {
							if (uploader.getStats().getUploadsInProgress() <= 0) {
								uploader.startUpload();
							}
						}
						return true;
					}
				}).setFileQueueErrorHandler(new FileQueueErrorHandler() {
					public boolean onFileQueueError(FileQueueErrorEvent fileQueueErrorEvent) {
						Window.alert("Upload of file " + fileQueueErrorEvent.getFile().getName()
								+ " failed due to ["
								+ fileQueueErrorEvent.getErrorCode().toString()
								+ "]: " + fileQueueErrorEvent.getMessage());
						
//						showDropPanelIfCompleted(fileQueueErrorEvent.getFile().getId());
						hasError = true;

						return true;
					}
				}).setUploadErrorHandler(new UploadErrorHandler() {
					public boolean onUploadError(UploadErrorEvent uploadErrorEvent) {
						Window.alert("Upload of file "
								+ uploadErrorEvent.getFile().getName()
								+ " failed due to ["
								+ uploadErrorEvent.getErrorCode().toString()
								+ "]: " + uploadErrorEvent.getMessage());
						
//						showDropPanelIfCompleted(uploadErrorEvent.getFile().getId());
						hasError = true;

						return true;
					}
				});

		VLayout uploaderHolder = new VLayout();
		uploaderHolder.addMember(uploader);
		uploaderHolder.setHeight(height);
		uploaderHolder.setAlign(VerticalAlignment.CENTER);
		addMember(uploaderHolder);
		setMembersMargin(3);

		showDropPanel();
		
		setAlign(VerticalAlignment.CENTER);
		
		setWidth(width);
		setHeight(height);
	}
	
	public void setUploadCompleteHandler(ReturnHandler handler) {
		this.completeHandler = handler;
	}
	
	private void showDropPanel() {
		
		fileIdList.clear();
		
		if (Uploader.isAjaxUploadWithProgressEventsSupported()) {
			final Label dropAreaLabel = new Label(I18N.message("DropFilesHere"));
			
			dropAreaLabel.setStyleName("dropFilesLabel");
			dropAreaLabel.setWidth((width - 3) + "px");
			dropAreaLabel.setHeight((height - 3) + "px");
//			dropAreaLabel.setWidth("200px");
			dropAreaLabel.addDragOverHandler(new DragOverHandler() {
				public void onDragOver(DragOverEvent event) {
					if (!uploader.getButtonDisabled()) {
						dropAreaLabel.addStyleName("dropFilesLabelHover");
					}
				}
			});
			dropAreaLabel.addDragLeaveHandler(new DragLeaveHandler() {
				public void onDragLeave(DragLeaveEvent event) {
					dropAreaLabel.removeStyleName("dropFilesLabelHover");
				}
			});
			dropAreaLabel.addDropHandler(new DropHandler() {
				public void onDrop(DropEvent event) {
					dropAreaLabel.removeStyleName("dropFilesLabelHover");

					if (uploader.getStats().getUploadsInProgress() <= 0) {
						fileIdList.clear();
					}

					uploader.addFilesToQueue(Uploader.getDroppedFiles(event.getNativeEvent()));
					event.preventDefault();
				}
			});
			
			if (dropAreaPanel == null) {
				dropAreaPanel = new HLayout();
				dropAreaPanel.addMember(dropAreaLabel);
				this.addMember(dropAreaPanel);
			}
			
			dropAreaPanel.show();
		}

		if (progressBarAndButtonPanel != null)
			progressBarAndButtonPanel.hide();
		
		setWidth(width);
		setHeight(height);

	}
	
//	private void clearPanel(Layout parent, Canvas canvas) {
//		if (canvas != null && parent.hasMember(canvas)){
//			canvas.removeFromParent();
//			parent.removeMember(canvas);
//			canvas.destroy();
//			canvas = null;
//		}
//	}
//	
	private void showProgress() {

		if (dropAreaPanel != null)
			dropAreaPanel.hide();

		if (progressBarAndButtonPanel == null) {
			progressBarAndButtonPanel = new VLayout();
			progressBarAndButtonPanel.setWidth100();
			progressBarAndButtonPanel.setHeight(height);
			
			progressLabel = ItemFactory.newLabelWithIcon(Util.imageUrl("running_task.gif"), "", false, 16, 0);
			progressLabel.setAlign(Alignment.LEFT);
			progressLabel.setWidth(width);
			
			// Create a Progress Bar for this file
			progressBar = new Progressbar();
			progressBar.setHeight((height/2 - 2)+ "px");
			progressBar.setVertical(false);
			progressBar.setTitle("");
//			progressBar.setHeight("18px");
//			progressBar.setWidth("200px");
	
			// Add Cancel Button Image
			final ImgButton cancelButton = ItemFactory.newImgButton("", "delete", 16, 16, false, false);
			cancelButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					for (String string : fileIdList) {
						uploader.cancelUpload(string, false);
					}
					progressBar.setPercentDone(0);
					cancelButton.removeFromParent();
				}
			});
	
			// Add the Bar and Button to the interface
			progressBarAndButtonPanel.addMember(progressLabel);
			HLayout inner = new HLayout();
			inner.setHeight((height/2)+"px");
			inner.setMembers(progressBar, cancelButton);
			progressBarAndButtonPanel.addMember(inner);
			progressBarAndButtonPanel.setWidth100();

			this.addMember(progressBarAndButtonPanel);
		}
		
		progressBarAndButtonPanel.show();
		progressBar.setPercentDone(0);
		
		setWidth(width + 100);

	}
	
	private void showDropPanelIfCompleted(String fileId) {
		if (fileIdList.isEmpty())
			return;
		
		if (fileId != null)
			fileIdList.remove(fileId);
		
		if (fileIdList.isEmpty()) {
			if (completeHandler != null)
				completeHandler.onReturn(null);

			// drop area 복구
			showDropPanel();
			hasError = false;
		}
	}
}