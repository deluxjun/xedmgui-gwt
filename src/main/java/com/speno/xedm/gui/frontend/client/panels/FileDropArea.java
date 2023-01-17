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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.widgetideas.client.ProgressBar;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.ImgButton;
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
public class FileDropArea extends HLayout {
	
//	private com.smartgwt.client.widgets.Label progressLabel;
	private String progressString;
	private String progressTitle;
	private HorizontalPanel switchPanel = new HorizontalPanel();
	private HorizontalPanel progressBarAndButtonPanel;
	private ProgressBar progressBar = null;
	private HorizontalPanel dropAreaPanel;
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
//		uploader.setUploadURL(GWT.getHostPageBaseURL() + "upload-directly");
	}
	
	public FileDropArea(int width, int height) {
		this.width = width;
		this.height = height;
		
		String maxSize = Session.get().getInfo().getConfig("upload.maxsize");
		if (maxSize.length() > 0) {
			maxSize += " MB";
		}
		else
			maxSize = "100 MB";
//		String maxSize = "100 MB";
		
		uploader
				.setButtonImageURL(ItemFactory.newImgIcon("upload.png").getSrc())
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
						double d = (double) uploadProgressEvent.getBytesComplete() / uploadProgressEvent.getBytesTotal();
						System.out.println("== " + d);
						int percent = (int) (100 * d);
						if (percent < 95) {
							String fileName = Util.strCut(uploadProgressEvent.getFile().getName(), 30, "..");
							progressString = fileName;
						}
						else {
							progressString = I18N.message("importing") + "..";
						}

						progressBar.setProgress(d);
						progressBar.setTitle(uploadProgressEvent.getFile().getName());
						
						return true;
					}
				})
				.setUploadCompleteHandler(new UploadCompleteHandler() {
					public boolean onUploadComplete(UploadCompleteEvent uploadCompleteEvent) {
						System.out.println("@ complete : " + uploadCompleteEvent.getFile().getName());
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
//						Window.alert("Upload of file "
//								+ uploadErrorEvent.getFile().getName()
//								+ " failed due to ["
//								+ uploadErrorEvent.getErrorCode().toString()
//								+ "]: " + uploadErrorEvent.getMessage());
						SC.warn( I18N.message("docuploaderror") +"("+ I18N.message("s.filename")+ ": " + uploadErrorEvent.getFile().getName() + ")");
						
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

		addMember(switchPanel);
		
		showDropPanel();
//		showProgress();
		
		setAlign(VerticalAlignment.CENTER);
		
		setWidth(width);
		setHeight(height);
	}
	private boolean bButton = true;
	
	public void setUploadCompleteHandler(ReturnHandler handler) {
		this.completeHandler = handler;
	}
	
	private void showDropPanel() {
		
		fileIdList.clear();

		if (Uploader.isAjaxUploadWithProgressEventsSupported()) {
			final Label dropAreaLabel = new Label(I18N.message("DropFilesHere"));
			
			dropAreaLabel.setStyleName("dropFilesLabel");
			dropAreaLabel.setWidth((width) + "px");
			dropAreaLabel.setHeight((height-6) + "px");
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
				dropAreaPanel = new HorizontalPanel();
				dropAreaPanel.add(dropAreaLabel);
				dropAreaPanel.setWidth(width + "px");
//				dropAreaPanel.setHeight(height + "px");
			}
		}

		switchPanel.clear();
		if (dropAreaPanel != null)
			switchPanel.add(dropAreaPanel);
		
//		setWidth(width);
//		setHeight(height);

	}
	
	private void showProgress() {
		if (progressBarAndButtonPanel == null) {
			progressBarAndButtonPanel = new HorizontalPanel();
			
			// Create a Progress Bar for this file
			progressBar = new ProgressBar(0.0d, 1.0d, 0.0, new ProgressBarTextFormatter());  
            progressBar.setHeight("16px");  
            progressBar.setWidth((width -20) + "px");
	
			// Add Cancel Button Image
			final ImgButton cancelButton = ItemFactory.newImgButton("", "delete", 16, 16, false, false);
			cancelButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					for (String string : fileIdList) {
						uploader.cancelUpload(string, false);
					}
					progressBar.setProgress(-1.0d);
				}
			});
	
			// Add the Bar and Button to the interface
			progressBarAndButtonPanel.add(progressBar);  
			progressBarAndButtonPanel.add(cancelButton);  
		}

		progressBar.setProgress(0.0d);

		switchPanel.clear();
		switchPanel.add(progressBarAndButtonPanel);
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
	
    protected class ProgressBarTextFormatter extends ProgressBar.TextFormatter {  
        @Override  
        protected String getText(ProgressBar bar, double curProgress) {  
            if (curProgress < 0) {  
                return "Cancelled";  
            }  
            return ((int) (100 * bar.getPercent())) + "%" + " " + progressString;  
        }  
    }  
}