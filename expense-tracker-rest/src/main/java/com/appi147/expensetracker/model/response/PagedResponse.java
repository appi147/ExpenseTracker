package com.appi147.expensetracker.model.response;

import java.util.List;

public record PagedResponse<T>(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages,
                               boolean last) {

}
