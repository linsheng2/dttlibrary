package com.dttandroid.dttlibrary.utils;

/**
 * @Author: lufengwen
 * @Date: 2015年12月2日 下午1:35:29
 * @Description:
 */
public class Combo3<T1, T2, T3> {
	private T1 v1;
	private T2 v2;
	private T3 v3;

	public T1 getV1() {
		return v1;
	}

	public void setV1(T1 v1) {
		this.v1 = v1;
	}

	public T2 getV2() {
		return v2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((v1 == null) ? 0 : v1.hashCode());
		result = prime * result + ((v2 == null) ? 0 : v2.hashCode());
		result = prime * result + ((v3 == null) ? 0 : v3.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Combo3<?, ?, ?> other = (Combo3<?, ?, ?>) obj;
		if (v1 == null) {
			if (other.v1 != null)
				return false;
		}
		else if (!v1.equals(other.v1))
			return false;
		if (v2 == null) {
			if (other.v2 != null)
				return false;
		}
		else if (!v2.equals(other.v2))
			return false;
		if (v3 == null) {
			if (other.v3 != null)
				return false;
		}
		else if (!v3.equals(other.v3))
			return false;
		return true;
	}

	public T3 getV3() {
		return v3;
	}

	public void setV3(T3 v3) {
		this.v3 = v3;
	}

	public void setV2(T2 v2) {
		this.v2 = v2;
	}

	public Combo3(T1 v1, T2 v2, T3 v3) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
	}
}
