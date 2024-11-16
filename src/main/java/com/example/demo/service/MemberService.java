package com.example.demo.service;

import com.example.demo.dto.MemberJoinDTO;

public interface MemberService {

    static class MidExistException extends Exception{

    }

    void join(MemberJoinDTO memberJoinDTO) throws MidExistException;

}
