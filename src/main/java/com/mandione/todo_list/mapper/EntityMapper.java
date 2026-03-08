package com.mandione.todo_list.mapper;

/**
 * Interface générique pour le mapping Entity <-> DTO.
 * Respect du principe OCP : Permet d'ajouter de nouveaux mappers sans modifier le code existant.
 * @param <E> Type de l'Entité
 * @param <D> Type du DTO
 */
public interface EntityMapper<E, D> {

    D toDto(E entity);

    E toEntity(D dto);
}
