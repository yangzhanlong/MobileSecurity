package org.me.mobilesecurity.utils;

import android.content.Context;
import android.content.res.AssetManager;

import org.me.mobilesecurity.utils.ModifyOffset.PointDouble;

import java.io.InputStream;

public class GPSUtils {

	/**
	 * 获得火星坐标的经纬度
	 * 
	 * @param latitude
	 *            :gps的纬度
	 * @param longitude
	 *            :gps的经度
	 * @return
	 */
	public static double[] parse(Context context, double latitude,
			double longitude) {
		InputStream stream = null;
		try {
			AssetManager assets = context.getAssets();
			stream = assets.open("axisoffset.dat");
			ModifyOffset instance = ModifyOffset.getInstance(stream);

			PointDouble pt = new PointDouble(longitude, latitude);
			PointDouble result = instance.s2c(pt);

			return new double[] { result.x, result.y };
		} catch (Exception e) {
		} finally {
			StreamUtils.closeIO(stream);
		}
		return null;
	}
}
