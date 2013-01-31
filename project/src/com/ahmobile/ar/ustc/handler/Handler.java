package com.ahmobile.ar.ustc.handler;

/**
 * 数据处理接口，所有的数据处理类都要实现该接口
 * @author MIL
 * @param <T>
 */
public interface Handler<T> {
	public T handle(String text);
}
