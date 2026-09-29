package org.example.movieservice.service.impl;

import org.example.movieservice.dto.request.CreateGenresRequest;
import org.example.movieservice.dto.response.GenreResponse;
import org.example.movieservice.exception.GenreNotFoundException;
import org.example.movieservice.model.Genre;
import org.example.movieservice.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreServiceImpl genreService;


    @Test
    void shouldSaveAllGenresWhenRepositoryIsEmpty() {
        //given
        CreateGenresRequest createGenresRequest =
                new CreateGenresRequest(Arrays.asList("Драма", "Фантастика", "Комедия"));
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
        List<String> testList = Arrays.asList("Драма", "Фантастика", "Комедия");
        CreateGenresRequest createGenresRequest =
                new CreateGenresRequest(testList);
        Set<String> testNames = new HashSet<>(testList);
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
        List<String> testList = Arrays.asList("Драма", "Фантастика", "Комедия");
        CreateGenresRequest request = new CreateGenresRequest(testList);
        when(genreRepository.findExistingNames(anySet()))
                .thenReturn(new HashSet<>(Set.of("Драма")));

        //when
        genreService.createGenres(request);

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
        List<Genre> testList = Arrays.asList(
                new Genre(UUID.randomUUID(),"Драма"),
                new Genre(UUID.randomUUID(),"Фантастика")
        );
        List<GenreResponse> genreResponses = testList
                .stream()
                .map(genre -> GenreResponse.builder()
                        .id(genre.getId())
                        .name(genre.getName())
                        .build())
                .toList();

        when(genreRepository.findAll()).thenReturn(testList);

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
        List<Genre> testGenresList = Arrays.asList(
                new Genre(UUID.randomUUID(),"Драма"),
                new Genre(UUID.randomUUID(),"Фантастика")
        );
        List<UUID> testIdsList = testGenresList
                .stream()
                .map(Genre::getId)
                .toList();

        Set<UUID> testIdsSet = new HashSet<>(testIdsList);
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