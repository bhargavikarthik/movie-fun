package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import static org.springframework.orm.jpa.vendor.Database.MYSQL;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {

    //@Value("${VCAP_SERVICES}")
    //String vCapServices;

    @Bean
    public DatabaseServiceCredentials getDatabaseServiceCredentials(@Value("${VCAP_SERVICES}") String vCapService){
        return new DatabaseServiceCredentials(vCapService);
    }

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    /*@Bean
    public DatabaseServiceCredentials serviceCredentials() {
        return new DatabaseServiceCredentials(vCapServices);
    } */

    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials albumserviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(albumserviceCredentials.jdbcUrl("albums-mysql"));
        HikariDataSource ds = new HikariDataSource();
        ds.setDataSource(dataSource);
        return ds;
    }

    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentials movieserviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(movieserviceCredentials.jdbcUrl("movies-mysql"));
        HikariDataSource ds = new HikariDataSource();
        ds.setDataSource(dataSource);
        return ds;
    }

    @Bean
    public HibernateJpaVendorAdapter getEntityManger(){
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabase(MYSQL);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        return hibernateJpaVendorAdapter;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean getLocalEntityManagerMovie(DataSource moviesDataSource, HibernateJpaVendorAdapter getEntityManger){
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(moviesDataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(getEntityManger);
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.*");
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("moviedb");
        return localContainerEntityManagerFactoryBean;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean getLocalEntityManagerAlbum(DataSource albumsDataSource, HibernateJpaVendorAdapter getEntityManger){
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(albumsDataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(getEntityManger);
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.*");
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("albumdb");
        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager platformTransactionManagerAlbum(EntityManagerFactory getLocalEntityManagerAlbum){
        JpaTransactionManager jpaTransactionManagerAlbum = new JpaTransactionManager() ;
        jpaTransactionManagerAlbum.setEntityManagerFactory(getLocalEntityManagerAlbum);
        return jpaTransactionManagerAlbum;
    }


    @Bean
    public PlatformTransactionManager platformTransactionManagerMovie(EntityManagerFactory getLocalEntityManagerMovie){
        JpaTransactionManager jpaTransactionManagerMovie = new JpaTransactionManager() ;
        jpaTransactionManagerMovie.setEntityManagerFactory(getLocalEntityManagerMovie);
        return jpaTransactionManagerMovie;
    }
}
