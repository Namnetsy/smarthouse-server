package com.gmail.roma.teodorovich.server.member.impl;

import com.gmail.roma.teodorovich.server.helper.RelationDatabaseHelper;
import com.gmail.roma.teodorovich.server.member.IMemberDao;
import com.gmail.roma.teodorovich.server.member.Member;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberDaoImpl implements IMemberDao {

    private static IMemberDao memberDao = null;

    private MemberDaoImpl() {}

    public static synchronized IMemberDao getInstance() {
        return (memberDao == null) ? memberDao = new MemberDaoImpl() : memberDao;
    }

    @Override
    public void addMember(Member member) throws SQLException {
        try (RelationDatabaseHelper helper = new RelationDatabaseHelper("INSERT INTO member VALUES (?, ?, ?)")) {
            helper.setValue(1, member.getUserId())
                    .setValue(2, member.getHubId())
                    .setValue(3, member.isAdmin())
                    .execute();
        }
    }

    @Override
    public List<Member> getMembers(String hubId) throws SQLException {
        try (RelationDatabaseHelper helper = new RelationDatabaseHelper("SELECT * FROM member WHERE hub_id = ?")) {
            List<Member> members = new ArrayList<>();

            RelationDatabaseHelper.Data data = helper.setValue(1, hubId).executeWithData();

            while (data.next()) {
                members.add(new Member((String)data.getValue(1),
                        (String)data.getValue(2), (boolean)data.getValue(3)));
            }

            return members;
        }
    }

    @Override
    public boolean checkIfExists(Member member) throws SQLException {
        try (RelationDatabaseHelper helper = new RelationDatabaseHelper(
                "SELECT EXISTS (SELECT user_id FROM member WHERE hub_id = ? AND user_id = ?)")) {
            RelationDatabaseHelper.Data data = helper.setValue(1, member.getHubId())
                    .setValue(2, member.getUserId())
                    .executeWithData();

            return (data.next()) && (boolean) data.getValue(1);
        }
    }

    @Override
    public boolean isUserMember(String userId) throws SQLException {
        try (RelationDatabaseHelper helper = new RelationDatabaseHelper(
                "SELECT EXISTS (SELECT hub_id FROM member WHERE user_id = ?)")) {
            RelationDatabaseHelper.Data data = helper.setValue(1, userId)
                    .executeWithData();

            return (data.next()) && (boolean) data.getValue(1);
        }
    }

    @Override
    public void deleteMember(String userId) throws SQLException {
        try (RelationDatabaseHelper helper = new RelationDatabaseHelper("DELETE FROM member WHERE user_id = ?")) {
            helper.setValue(1, userId).execute();
        }
    }

    @Override
    public void setAdmin(Member member, boolean value) throws SQLException {
        try (RelationDatabaseHelper helper =new RelationDatabaseHelper(
                "UPDATE member SET is_admin = ? WHERE hub_id = ? AND user_id = ?")) {
            helper.setValue(1, value)
                    .setValue(2, member.getHubId())
                    .setValue(3, member.getUserId()).execute();
        }
    }

    @Override
    public List<String> getFcmTokens(String hubId) throws SQLException {
        try (RelationDatabaseHelper helper = new RelationDatabaseHelper(
                "SELECT fcm_token FROM user_account WHERE id = (SELECT user_id FROM member WHERE hub_id = ?)")) {
            List<String> tokens = new ArrayList<>();

            RelationDatabaseHelper.Data data = helper.setValue(1, hubId).executeWithData();

            while (data.next()) {
                tokens.add((String)data.getValue(1));
            }

            return tokens;
        }
    }

    @Override
    public void deleteAllMembers(String hubId) throws SQLException {
        try (RelationDatabaseHelper helper = new RelationDatabaseHelper("DELETE FROM member WHERE hub_id = ?")) {
            helper.setValue(1, hubId).execute();
        }
    }

}
