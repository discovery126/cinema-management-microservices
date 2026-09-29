package org.example.movieservice.service.impl;

import org.example.movieservice.dto.request.CreateGenresRequest;
import org.example.movieservice.dto.response.GenreResponse;
import org.example.movieservice.exception.GenreNotFoundException;
import org.example.movieservice.mapper.GenreMapper;
import org.example.movieservice.model.Genre;
import org.example.movieservice.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    private GenreServiceImpl genreService;

    private GenreMapper genreMapper;
    private CreateGenresRequest createGenresRequest;
    private Set<String> testNames;

    @BeforeEach
    void setup() {
        genreMapper = new GenreMapper();

        genreService = new GenreServiceImpl(genreRepository, genreMapper);

        List<String> testList = Arrays.asList("Драма", "Фантастика", "Комедия");
        createGenresRequest = new CreateGenresRequest(testList);
        testNames = new HashSet<>(testList);
    }

    @Test
    void shouldSaveAllGenresWhenRepositoryIsEmpty() {
        //given
        when(genreRepository.findExistingNames(anySet())).thenReturn(new HashSet<>());

        //when
        genreService.createGenres(createGenresRequest);

        //then
        verify(genreRepository).findExistingNames(anySet());
        verify(genreRepository).saveAll(argThat(genres -> {
            List<Genre> list = new ArrayList<>();
            genres.forEach(list::add);
            return list.size() == 3;
        }));
    }

    @Test
    void shouldNotSaveGenresWhenGenresAlreadyExist() {
        //given
        when(genreRepository.findExistingNames(anySet())).thenReturn(testNames);

        //when
        genreService.createGenres(createGenresRequest);

        //then
        verify(genreRepository).findExistingNames(anySet());
        verify(genreRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldSaveNewGenresWhenSomeDoNotExist() {
        //given
        when(genreRepository.findExistingNames(anySet()))
                .thenReturn(new HashSet<>(Set.of("Драма")));

        //when
        genreService.createGenres(createGenresRequest);

        //then
        verify(genreRepository).saveAll(argThat(genres -> {
            List<Genre> list = new ArrayList<>();
            genres.forEach(list::add);
            return list.size() == 2
                    && list.stream().anyMatch(g -> g.getName().equals("Фантастика"))
                    && list.stream().anyMatch(g -> g.getName().equals("Комедия"));
        }));
    }

    @Test
    void shouldReturnAllGenresWhenRepositoryNotEmpty() {
        //given
        List<Genre> testGenresList = List.of(
                new Genre(UUID.randomUUID(), "Драма"),
                new Genre(UUID.randomUUID(), "Фантастика")
        );
        List<GenreResponse> genreResponses = testGenresList.stream()
                .map(genreMapper::toGenreResponse)
                .toList();

        when(genreRepository.findAll()).thenReturn(testGenresList);

        //when
        List<GenreResponse> resultGenres = genreService.getGenres();

        //then
        verify(genreRepository).findAll();
        assertNotNull(resultGenres);
        assertEquals(genreResponses, resultGenres);
    }

    @Test
    void shouldReturnFoundGenresFromRepositoryWhenRepositoryNotEmpty() {
        //given
        List<Genre> testGenresList = List.of(
                new Genre(UUID.randomUUID(), "Драма"),
                new Genre(UUID.randomUUID(), "Фантастика")
        );
        Set<UUID> testIdsSet = testGenresList.stream()
                .map(Genre::getId)
                .collect(java.util.stream.Collectors.toSet());
        Set<Genre> testGenresSet = new HashSet<>(testGenresList);

        when(genreRepository.findAllById(testIdsSet)).thenReturn(testGenresList);

        //when
        Set<Genre> resultGenres = genreService.findAllById(testIdsSet);

        //then
        verify(genreRepository).findAllById(testIdsSet);
        assertNotNull(resultGenres);
        assertEquals(testGenresSet, resultGenres);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenRepositoryReturnsNothing() {
        // given
        Set<UUID> testIds = Set.of(UUID.randomUUID(), UUID.randomUUID());
        when(genreRepository.findAllById(testIds)).thenReturn(List.of());

        // when && then
        assertThatThrownBy(() -> genreService.findAllById(testIds))
                .isInstanceOf(GenreNotFoundException.class)
                .satisfies(ex -> {
                    GenreNotFoundException e = (GenreNotFoundException) ex;
                    assertThat(e.getNotFoundIds()).containsAll(testIds);
                });
    }

    @Test
    void shouldThrowNotFoundExceptionWhenRepositoryReturnsSomeGenres() {
        // given
        UUID existingId = UUID.randomUUID();
        UUID missingId = UUID.randomUUID();
        List<Genre> found = List.of(new Genre(existingId, "Драма"));
        Set<UUID> testIds = Set.of(existingId, missingId);

        when(genreRepository.findAllById(testIds)).thenReturn(found);

        // when && then
        assertThatThrownBy(() -> genreService.findAllById(testIds))
                .isInstanceOf(GenreNotFoundException.class)
                .satisfies(ex -> {
                    GenreNotFoundException e = (GenreNotFoundException) ex;
                    assertThat(e.getNotFoundIds()).containsExactly(missingId);
                });
    }
}