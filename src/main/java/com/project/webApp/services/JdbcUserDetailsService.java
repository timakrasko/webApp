package com.project.webApp.services;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.Optional;

public class JdbcUserDetailsService extends MappingSqlQuery<UserDetails> implements UserDetailsService {
    public JdbcUserDetailsService(DataSource ds){
        super(ds, """
                select 
                u.username,
                u.password,
                GROUP_CONCAT(ur.roles) as roles
                from user u
                left join user_role ur on ur.user_id = u.id
                where u.username = :username
                group by u.id
                """);
        this.declareParameter(new SqlParameter("username", Types.VARCHAR));
        this.compile();
    }
    @Override
    protected UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .username(rs.getString("username"))
                .password(rs.getString("password"))
                .authorities(rs.getString("roles"))
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(this.findObjectByNamedParam(Map.of("username",username)))
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }
}
