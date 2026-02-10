package service;


public interface WebServiceStubGenerator {
	public <S> S getStub(String url) throws Exception;
}
