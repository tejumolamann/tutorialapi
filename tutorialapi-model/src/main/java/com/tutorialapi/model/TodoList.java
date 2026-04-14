package com.tutorialapi.model;

public class TodoList {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public TodoList setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public TodoList setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        TodoList todoList = (TodoList) o;
        return id.equals(todoList.id) && name.equals(todoList.name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TodoList{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
