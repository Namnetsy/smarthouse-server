package com.gmail.roma.teodorovich.server.tests;

import com.gmail.roma.teodorovich.server.PostgreSqlUnitTest;
import com.gmail.roma.teodorovich.server.UserHelper;
import com.gmail.roma.teodorovich.server.member.Member;
import com.gmail.roma.teodorovich.server.member.impl.MemberDaoImpl;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("[DAO:PostgreSQL] Member Tests")
public class MemberDaoTest extends PostgreSqlUnitTest {

    @Test
    @DisplayName("Add Member")
    public void addMember() throws SQLException {
        try (UserHelper helper = new UserHelper().addToHub()) {
            Member member = new Member(helper.getUser().getId(), helper.getHub().getId());
            MemberDaoImpl.getInstance().addMember(member);

            Assertions.assertTrue(MemberDaoImpl.getInstance().checkIfExists(member));
        }
    }

    @Test
    @DisplayName("Delete Member")
    public void deleteMember() throws SQLException {
        try (UserHelper helper = new UserHelper().addToHub()) {
            MemberDaoImpl.getInstance().addMember(new Member(helper.getUser().getId(), helper.getHub().getId()));

            MemberDaoImpl.getInstance().deleteMember(helper.getUser().getId());

            Assertions.assertFalse(MemberDaoImpl.getInstance()
                    .checkIfExists(new Member(helper.getUser().getId(), helper.getHub().getId())));
        }
    }

    @Test
    @DisplayName("Delete All Members")
    public void deleteAllMembers() throws SQLException {
        try (UserHelper helper = new UserHelper().addToHub()) {
            MemberDaoImpl.getInstance().addMember(new Member(helper.getUser().getId(), helper.getHub().getId()));
            MemberDaoImpl.getInstance().deleteAllMembers(helper.getHub().getId());

            Assertions.assertFalse(MemberDaoImpl.getInstance()
                    .checkIfExists(new Member(helper.getUser().getId(), helper.getHub().getId())));
        }
    }

    @Test
    @DisplayName("Get List Of Members")
    public void getListOfMembers() throws SQLException {
        try (UserHelper helper = new UserHelper().addToHub()) {
            MemberDaoImpl.getInstance()
                    .addMember(new Member(helper.getUser().getId(), helper.getHub().getId()));
            List<Member> members = MemberDaoImpl.getInstance().getMembers(helper.getHub().getId());

            Assertions.assertFalse(members.isEmpty());
            Assertions.assertEquals(helper.getHub().getId(), members.get(0).getHubId());
            Assertions.assertEquals(helper.getUser().getId(), members.get(0).getUserId());
        }
    }

    @Test
    @DisplayName("Check If Member Exists")
    public void checkIfMemberExists() throws SQLException {
        try (UserHelper helper = new UserHelper().addToHub()) {
            MemberDaoImpl.getInstance().addMember(new Member(helper.getUser().getId(), helper.getHub().getId()));

            Assertions.assertTrue(MemberDaoImpl.getInstance()
                    .checkIfExists(new Member(helper.getUser().getId(), helper.getHub().getId())));
        }
    }

    @Test
    @DisplayName("Check If Member Doesn't Exist")
    public void checkIfMemberDoesntExist() {
        try (UserHelper helper = new UserHelper().addToHub()) {
            MemberDaoImpl.getInstance().addMember(new Member("1234567", helper.getHub().getId()));

            MemberDaoImpl.getInstance()
                    .checkIfExists(new Member(helper.getUser().getId(), helper.getHub().getId()));

            Assertions.fail();
        } catch (SQLException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    @DisplayName("Get Tokens Of All Members")
    public void getTokensOfAllMembers() throws SQLException {
        try (UserHelper helper = new UserHelper().addToHub()) {
            MemberDaoImpl.getInstance()
                    .addMember(new Member(helper.getUser().getId(), helper.getHub().getId()));
            List<String> tokens = MemberDaoImpl.getInstance().getFcmTokens(helper.getHub().getId());

            Assertions.assertFalse(tokens.isEmpty());
            Assertions.assertEquals(helper.getUser().getFcmToken(), tokens.get(0));
        }
    }

}
