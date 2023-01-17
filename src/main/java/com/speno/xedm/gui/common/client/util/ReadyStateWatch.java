package com.speno.xedm.gui.common.client.util;

import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Frame;

public class ReadyStateWatch {
	public static enum ReadyState {
		COMPLETE {
			@Override
			public String toString() {
				return "complete";
			}
		},
		LOADING {
			@Override
			public String toString() {
				return "loading";
			}
		},
		INTERACTIVE {
			@Override
			public String toString() {
				return "interactive";
			}
		},
		UNINITIALIZED {
			@Override
			public String toString() {
				return "uninitialized";
			}
		}
	}

	Frame iframe;

	public ReadyStateWatch(Frame iframe) {
		this.iframe = iframe;
		addNativeReadyStateWatch(this, iframe.getElement().<IFrameElement> cast());
	}

	public HandlerRegistration addReadyStateChangeHandler(
			ValueChangeHandler<ReadyState> handler) {
		return iframe.addHandler(handler, ValueChangeEvent.getType());
	}

	public ReadyState getReadyState() {
		try {
			return ReadyState.valueOf(iframe.getElement().getPropertyString("readyState").toUpperCase()); 
//			return ReadyState.valueOf(iframe.getElement().getPropertyString("readyState"));
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unused")
	// used in native function
	private void fireReadyStateChange() {
		iframe.fireEvent(new ValueChangeEvent<ReadyState>(getReadyState()) {});
	}

	private static native void addNativeReadyStateWatch(ReadyStateWatch self, IFrameElement e)/*-{
		var handleStateChange = function() {
			self.@com.speno.xedm.gui.common.client.util.ReadyStateWatch::fireReadyStateChange()();
		};
		if (e.addEventListener) {
			e.addEventListener("onreadystatechange", handleStateChange, false);
		} else if (e.attachEvent) {
			e.attachEvent("onreadystatechange", handleStateChange);
		} else {
			e.onreadystatechange = handleStateChange;
		}
	}-*/;
}