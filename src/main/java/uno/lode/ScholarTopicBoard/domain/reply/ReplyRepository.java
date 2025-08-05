package uno.lode.ScholarTopicBoard.domain.reply;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

	//List<Reply> findByTopicId(Long topicId);

	/**
	 * Retrieves all top-level replies (i.e., replies with no parent) for the given topic ID,
	 * including their nested children up to two levels deep.
	 * <p>
	 * This method uses an {@link jakarta.persistence.EntityGraph} to eagerly fetch the following associations:
	 * <ul>
	 *   <li>{@code author} - the author of the root reply</li>
	 *   <li>{@code children} - the first-level children of the root reply</li>
	 *   <li>{@code children.author} - the authors of the first-level children</li>
	 *   <li>{@code children.children} - the second-level children (i.e., grandchildren) of the root reply</li>
	 *   <li>{@code children.children.author} - the authors of the second-level children</li>
	 * </ul>
	 * Replies beyond the second level of depth are not fetched and may trigger additional queries if accessed.
	 *
	 * @param topicId the ID of the topic whose root replies are to be retrieved
	 * @return a list of top-level {@link Reply} entities with their nested children and authors eagerly loaded
	 */
	@EntityGraph(attributePaths = {
		    "author",
		    "children",
		    "children.author",
		    "children.children",
		    "children.children.author"
		})
	List<Reply> findByTopicIdAndParentIsNull(Long topicId);

}
