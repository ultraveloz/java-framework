package br.com.orlandoburli.framework.core.web;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.orlandoburli.framework.core.be.BaseBe;
import br.com.orlandoburli.framework.core.be.exceptions.persistence.ListException;
import br.com.orlandoburli.framework.core.dao.BaseCadastroDao;
import br.com.orlandoburli.framework.core.dao.DAOManager;
import br.com.orlandoburli.framework.core.dao.DaoUtils;
import br.com.orlandoburli.framework.core.vo.BaseVo;
import br.com.orlandoburli.framework.core.web.filters.IgnoreMethodAuthentication;
import br.com.orlandoburli.framework.core.web.filters.InjectionFilter;

public abstract class BaseConsultaAction<E extends BaseVo, F extends BaseCadastroDao<E>, G extends BaseBe<E, F>> extends BaseAction {

	private static final long serialVersionUID = 1L;

	private int PageSize;
	private int PageNumber;

	private String opcao;

	private String PesquisarPor;
	private String ParametroPesquisa;

	public void execute() {
		doBeforeExecute();
		if (getOpcao() != null && getOpcao().equalsIgnoreCase("grid")) {
			grid();
		} else {
			forward(getJspConsulta());
		}
	}

	public void doBeforeExecute() {

	}

	@IgnoreMethodAuthentication
	public void grid() {
		DAOManager manager = new DAOManager();
		try {
			@SuppressWarnings("unchecked")
//			getBEClass().getConstructor(parameterTypes)
			G be = (G) DaoUtils.getNewBe((Class<BaseBe<BaseVo, BaseCadastroDao<BaseVo>>>) getBEClass(), manager); //(G) getBEClass().newInstance();

			@SuppressWarnings("unchecked")
			E filter = (E) getVOClass().newInstance();

			doBeforeFilter(filter, be, getRequest(), getResponse());

			try {
				List<E> list = be.getList(filter, "", getOrderFields(), PageSize, PageNumber);
				int pageCount = be.getPageCount(filter, "", PageSize);

				doBeforeSetList(list, pageCount, PageSize, PageNumber);

				getRequest().setAttribute("listSource", list);
				getRequest().setAttribute("pageNumber", PageNumber);
				getRequest().setAttribute("pageSize", PageSize);
				getRequest().setAttribute("pageCount", pageCount);
			} catch (ListException e) {
				e.printStackTrace();
				getRequest().setAttribute("mensagemErro", e.getMessage());
			}

			InjectionFilter injection = new InjectionFilter();
			injection.setContext(getContext());
			injection.setRequest(getRequest());
			injection.setResponse(getResponse());
			injection.doFilter(this);

			forward(getJspGridConsulta());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			manager.commit();
		}
	}

	public void doBeforeSetList(List<E> list, int pageCount, int pageSize, int pageNumber) {

	}

	protected Class<?> getVOClass() {
		return ((Class<?>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
	}

	protected Class<?> getDAOClass() {
		return ((Class<?>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1]);
	}

	protected Class<?> getBEClass() {
		return ((Class<?>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[2]);
	}

	public abstract String getJspConsulta();

	public abstract String getJspGridConsulta();

	public abstract String getOrderFields();

	public abstract void doBeforeFilter(E filter, G be, HttpServletRequest request, HttpServletResponse response);

	public int getPageSize() {
		return PageSize;
	}

	public void setPageSize(int pageSize) {
		PageSize = pageSize;
	}

	public int getPageNumber() {
		return PageNumber;
	}

	public void setPageNumber(int pageNumber) {
		PageNumber = pageNumber;
	}

	public String getOpcao() {
		return opcao;
	}

	public void setOpcao(String opcao) {
		this.opcao = opcao;
	}

	public String getPesquisarPor() {
		return PesquisarPor;
	}

	public void setPesquisarPor(String pesquisarPor) {
		PesquisarPor = pesquisarPor;
	}

	public String getParametroPesquisa() {
		return ParametroPesquisa;
	}

	public void setParametroPesquisa(String parametroPesquisa) {
		ParametroPesquisa = parametroPesquisa;
	}
}