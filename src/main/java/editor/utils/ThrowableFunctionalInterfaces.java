/*-
 * Copyright (c) 2016, NGSPipes Team <ngspipes@gmail.com>
 * All rights reserved.
 *
 * This file is part of NGSPipes <http://ngspipes.github.io/>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package editor.utils;

public class ThrowableFunctionalInterfaces {

    @FunctionalInterface
    public interface Consumer<T> {
        void accept(T t) throws Exception;

        default Consumer<T> andThen(Consumer<? super T> after){
            return (T t)->{
                this.accept(t);
                after.accept(t);
            };
        }
    }

    @FunctionalInterface
    public interface BiConsumer<T,U> {
        void accept(T t, U u) throws Exception;

        default BiConsumer<T,U> andThen(BiConsumer<? super T,? super U> after){
            return (T t, U u)->{
                this.accept(t, u);
                after.accept(t, u);
            };
        }
    }

    @FunctionalInterface
    public interface Predicate<T> {
        boolean test(T t) throws Exception;

        default Predicate<T> and(Predicate<? super T> other){
            return (T t) -> this.test(t) && other.test(t);
        }

        default Predicate<T> or(Predicate<? super T> other) {
            return (T t) -> this.test(t) || other.test(t);
        }

        default Predicate<T> negate(){
            return (T t) -> !this.test(t);
        }
    }

    @FunctionalInterface
    public interface BiPredicate<T,U> {
        boolean test(T t, U u) throws Exception;

        default BiPredicate<T,U> and(BiPredicate<? super T,? super U> other){
            return (T t, U u) -> this.test(t,u) && other.test(t,u);
        }

        default BiPredicate<T,U> or(BiPredicate<? super T,? super U> other) {
            return (T t, U u) -> this.test(t,u) || other.test(t,u);
        }

        default BiPredicate<T,U> negate(){
            return (T t, U u) -> !this.test(t,u);
        }
    }

    @FunctionalInterface
    public interface Function<T,R> {
        R apply(T t) throws Exception;

        default <V> Function<T,V> andThen(Function<? super R,? extends V> after) {
            return (T t) -> after.apply(this.apply(t));
        }

        default <V> Function<V,R> compose(Function<? super V,? extends T> before) {
            return (V v) -> this.apply(before.apply(v));
        }
    }

    @FunctionalInterface
    public interface BiFunction<T,U,R> {
        R apply(T t, U u) throws Exception;

        default <V> BiFunction<T,U,V> andThen(Function<? super R,? extends V> after) {
            return (T t, U u) -> after.apply(this.apply(t,u));
        }
    }

    @FunctionalInterface
    public interface Supplier<T> {
        T get() throws Exception;
    }

}
