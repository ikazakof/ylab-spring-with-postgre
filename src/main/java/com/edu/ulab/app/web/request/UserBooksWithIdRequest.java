package com.edu.ulab.app.web.request;

import lombok.Data;

import java.util.List;

@Data
public class UserBooksWithIdRequest {
    private UserRequest userRequest;
    private List<BookWithIdRequest> bookWithIdRequests;
}
