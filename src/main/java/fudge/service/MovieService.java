package fudge.service;

import fudge.model.movie.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface MovieService {
    Page<Movie> getMovies(Pageable pageable);

    ResponseEntity<Movie> getMovie(Integer movieId);
}
