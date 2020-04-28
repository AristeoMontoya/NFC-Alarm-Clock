package com.nfcalarmclock;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

/**
 */
public class NacSpeakFrequencyDialog
	extends NacSpinnerDialog
	implements NacDialog.OnBuildListener,
		NacDialog.OnShowListener
{

	/**
	 */
	public NacSpeakFrequencyDialog()
	{
		this.setOnBuildListener(this);
		this.addOnShowListener(this);
	}

	/**
	 * Build the dialog.
	 */
	@Override
	public void onBuildDialog(NacDialog dialog, AlertDialog.Builder builder)
	{
		Context context = dialog.getRoot().getContext();
		NacSharedConstants cons = new NacSharedConstants(context);

		builder.setTitle(cons.getSpeakFrequency());
		dialog.setPositiveButton(cons.getOk());
		dialog.setNegativeButton(cons.getCancel());
	}

	/**
	 * Show the dialog.
	 */
	@Override
	public void onShowDialog(NacDialog dialog, View root)
	{
		super.onShowDialog(dialog, root);

		Context context = dialog.getRoot().getContext();
		NacSharedConstants cons = new NacSharedConstants(context);
		int index = this.getDataInt();
		int length = 31;
		String[] values = new String[length];

		values[0] = cons.getFrequencyOnce();

		for (int i=1; i < length; i++)
		{
			values[i] = String.valueOf(i);
		}

		this.setDisplayedValues(values);
		this.setValue(index);
	}

}
