package com.tutorialapi.db.service.sqlite;

import com.tutorialapi.db.DataSourceExtension;
import com.tutorialapi.db.exception.ConflictException;
import com.tutorialapi.model.TodoList;
import com.tutorialapi.model.user.RapidApiPrincipal;
import com.tutorialapi.model.user.Subscription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DataSourceExtension.class)
class SqliteTodoListServiceIT {
    private final SqliteTodoListService todoListService;

    private final RapidApiPrincipal principal1 = new RapidApiPrincipal(
            "user1", Subscription.BASIC, "proxy-secret");

    private final RapidApiPrincipal principal2 = new RapidApiPrincipal(
            "user2", Subscription.BASIC, "proxy-secret");

    SqliteTodoListServiceIT(DataSource dataSource) {
        this.todoListService = new SqliteTodoListService(dataSource);
    }

    @Test
    void testGetWithNoLists() {
        Optional<TodoList> fetched = todoListService.get(principal1, "id");
        assertTrue(fetched.isEmpty());
    }

    @Test
    void testGetWithWrongPrincipal() {
        TodoList list = new TodoList().setId("id").setName("name");
        assertTrue(todoListService.create(principal1, list));

        Optional<TodoList> fetched = todoListService.get(principal2, list.getId());
        assertTrue(fetched.isEmpty());
    }

    @Test
    void testGetWithWrongId() {
        TodoList wrongId = new TodoList().setId("id").setName("name");
        assertTrue(todoListService.create(principal1, wrongId));

        Optional<TodoList> fetched = todoListService.get(principal1, "wrong");
        assertTrue(fetched.isEmpty());
    }

    @Test
    void testGetWithOneList() {
        TodoList list = new TodoList().setId("id").setName("name");
        assertTrue(todoListService.create(principal1, list));

        Optional<TodoList> fetched = todoListService.get(principal1, list.getId());
        assertTrue(fetched.isPresent());
        assertEquals(fetched.get(), list);
    }

    @Test
    void testGetWithMultipleLists() {
        TodoList list1 = new TodoList().setId("id1").setName("name");
        TodoList list2 = new TodoList().setId("id2").setName("name");
        assertTrue(todoListService.create(principal1, list1));
        assertTrue(todoListService.create(principal1, list2));

        Optional<TodoList> fetched = todoListService.get(principal1, list1.getId());
        assertTrue(fetched.isPresent());
        assertEquals(list1, fetched.get());
    }

    @Test
    void testGetAllWithNoList() {
        List<TodoList> fetched = todoListService.getAll(principal1);
        assertTrue(fetched.isEmpty());
    }

    @Test
    void testGetAllWithWrongPrincipal() {
        TodoList list = new TodoList().setId("id").setName("name");
        assertTrue(todoListService.create(principal1, list));

        List<TodoList> fetched = todoListService.getAll(principal2);
        assertTrue(fetched.isEmpty());
    }

    @Test
    void testGetAllWithOneList() {
        TodoList list = new TodoList().setId("id").setName("name");
        assertTrue(todoListService.create(principal1, list));

        List<TodoList> fetched = todoListService.getAll(principal1);
        assertEquals(1, fetched.size());
        assertEquals(list, fetched.get(0));
    }

    @Test
    void testGetAllWithMultipleLists() {
        TodoList list1 = new TodoList().setId("id1").setName("name1");
        TodoList list2 = new TodoList().setId("id2").setName("name2");
        assertTrue(todoListService.create(principal1, list1));
        assertTrue(todoListService.create(principal1, list2));

        List<TodoList> fetched = todoListService.getAll(principal1);
        assertEquals(2, fetched.size());
        assertEquals(list1, fetched.get(0));
        assertEquals(list2, fetched.get(1));
    }

    @Test
    void testCreateConflict() {
        TodoList list = new TodoList().setId("id").setName("name");
        assertTrue(todoListService.create(principal1, list));

        ConflictException exception = assertThrows(ConflictException.class, () -> todoListService.create(principal1, list));
        assertEquals("Todo list already exists", exception.getMessage());
    }

    @Test
    void testCreateConflictDifferentName() {
        TodoList list1 = new TodoList().setId("id").setName("name1");
        TodoList list2 = new TodoList().setId("id").setName("name2");
        assertTrue(todoListService.create(principal1, list1));

        ConflictException exception = assertThrows(ConflictException.class, ()-> todoListService.create(principal1, list2));
        assertEquals("Todo list already exists", exception.getMessage());
    }

    @Test
    void testCreateConflictDifferentUser() {
        TodoList list1 = new TodoList().setId("id").setName("name");
        TodoList list2 = new TodoList().setId("id").setName("name");
        assertTrue(todoListService.create(principal1, list1));
        assertTrue(todoListService.create(principal2, list2));

        Optional<TodoList> fetched1 = todoListService.get(principal1, list1.getId());
        Optional<TodoList> fetched2 = todoListService.get(principal2, list2.getId());
        assertTrue(fetched1.isPresent());
        assertTrue(fetched2.isPresent());
    }

    @Test
    void testUpdateMissing() {
        TodoList list = new TodoList().setId("id").setName("name");
        assertFalse(todoListService.update(principal1, list));
    }

    @Test
    void testUpdateExistsButSame() {
        TodoList list = new TodoList().setId("id").setName("name");
        assertTrue(todoListService.create(principal1, list));
        assertFalse(todoListService.update(principal1, list));
    }

    @Test
    void testUpdateWrongUser() {
        TodoList list = new TodoList().setId("id").setName("name");
        assertTrue(todoListService.create(principal1, list));

        list.setName("updated");
        assertFalse(todoListService.update(principal2, list));

        Optional<TodoList> fetched = todoListService.get(principal1, list.getId());
        assertTrue(fetched.isPresent());
        assertEquals("name", fetched.get().getName());
    }

    @Test
    void testUpdateDifferentName() {
        TodoList list = new TodoList().setId("id").setName("name");
        assertTrue(todoListService.create(principal1, list));
        list.setName("updated");
        assertTrue(todoListService.update(principal1, list));

        Optional<TodoList> fetched = todoListService.get(principal1, list.getId());
        assertTrue(fetched.isPresent());
        assertEquals(list, fetched.get());
    }

    @Test
    void testDeleteMissing() {
        Optional<TodoList> deleted = todoListService.delete(principal1, "missing");
        assertFalse(deleted.isPresent());
    }

    @Test
    void testDeleteWrongUser() {
        TodoList list = new TodoList().setId("id").setName("name");
        assertTrue(todoListService.create(principal1, list));

        Optional<TodoList> deleted = todoListService.delete(principal2, list.getId());
        assertFalse(deleted.isPresent());

        Optional<TodoList> fetched = todoListService.get(principal1, list.getId());
        assertTrue(fetched.isPresent());
    }

    @Test
    void testDeleteSuccess() {
        TodoList list = new TodoList().setId("id").setName("name");
        assertTrue(todoListService.create(principal1, list));

        Optional<TodoList> deleted = todoListService.delete(principal1, list.getId());
        assertTrue(deleted.isPresent());

        Optional<TodoList> fetched = todoListService.get(principal1, list.getId());
        assertTrue(fetched.isEmpty());
    }

    @Test
    void testTruncateNone() {
        int deleted = todoListService.truncate();
        assertEquals(0, deleted);
    }

    @Test
    void testTruncateWithSome() {
        TodoList list1 = new TodoList().setId("id1").setName("name");
        TodoList list2 = new TodoList().setId("id2").setName("name");

        assertTrue(todoListService.create(principal1, list1));
        assertTrue(todoListService.create(principal1, list2));

        int deleted = todoListService.truncate();
        assertEquals(2, deleted);
    }
}