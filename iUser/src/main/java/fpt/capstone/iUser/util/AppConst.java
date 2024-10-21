package fpt.capstone.iUser.util;

public interface AppConst {

    String SEARCH_OPERATOR = "(\\w+?)(:|<|>)(.*)";
    String SEARCH_SPEC_OPERATOR = "(\\w+?)([><!~*:`@])\\s*(.*)";
    String SORT_BY = "(\\w+?)(:)(.*)";
    String ADDRESS_REGEX = "address_";
    String ROLE_REGEX = "role_";
}
