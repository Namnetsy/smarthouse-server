package com.gmail.roma.teodorovich.server.member;

import java.sql.SQLException;
import java.util.List;

public interface IMemberDao {

    void addMember(Member member) throws SQLException;

    void deleteMember(String userId) throws SQLException;

    void setAdmin(Member member, boolean value) throws SQLException;

    void deleteAllMembers(String hubId) throws SQLException;

    boolean checkIfExists(Member member) throws SQLException;

    boolean isUserMember(String userId) throws SQLException;

    List<Member> getMembers(String hubId) throws SQLException;

    List<String> getFcmTokens(String hubId) throws SQLException;

}
